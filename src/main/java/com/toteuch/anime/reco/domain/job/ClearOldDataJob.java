package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.ApplicationContextHolder;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.profile.UserSimilarityService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearOldDataJob {
    private static final Logger log = LoggerFactory.getLogger(ClearOldDataJob.class);

    private final JobTaskService jobTaskService;
    private final int pageSize;
    private final Long userCountToRefresh;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final UserSimilarityService userSimilarityService;

    private JobTask jobTask;
    private Profile profile;
    private boolean hasFailed = false;

    public ClearOldDataJob(JobTask jobTask) {
        this.jobTask = jobTask;
        this.jobTaskService = ApplicationContextHolder.getBean(JobTaskService.class);
        int corePoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.clear-old-data-job.core-pool-size"));
        int maxPoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.clear-old-data-job.max-pool-size"));
        this.pageSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.clear-old-data-job.chunk-size"));
        this.userCountToRefresh = Long.parseLong((String) ApplicationContextHolder.getProperty(
                "app.domain.job.clear-old-data-job.user-count-to-refresh"));
        this.taskExecutor = taskExecutor(corePoolSize, maxPoolSize);
        this.userSimilarityService = ApplicationContextHolder.getBean(UserSimilarityService.class);
    }

    public void start() throws AnimeRecoException {
        jobTask = jobTaskService.start(jobTask.getId());
        profile = jobTask.getProfile();
        log.debug("Gathering data to start clearing down old data for Profile {} ({})", profile.getSub(), profile.getId());

        // READER
        Page<UserSimilarity> similarityToProcessPage = userSimilarityService.findSimilarities(profile.getSub(),
                PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "lastUpdate")));
        long totalSimilarity = similarityToProcessPage.getTotalElements();
        long totalPageToProcess = similarityToProcessPage.getTotalPages();
        if (totalSimilarity > userCountToRefresh) {
            totalSimilarity = userCountToRefresh;
            totalPageToProcess = (long) Math.ceil(totalSimilarity / pageSize);
        }
        log.debug("{} data to clear", totalSimilarity);
        jobTask = jobTaskService.setReadItemCount(jobTask, totalSimilarity);

        log.debug("Number of pages to process {}", totalPageToProcess);
        log.info("Starting to clearing old data for Profile {} ({})...", profile.getSub(), profile.getId());
        List<UserSimilarity> similarityToProcessList = similarityToProcessPage.getContent();
        while (similarityToProcessList != null && !hasFailed && !jobTaskService.isAbandonned(jobTask)) {
            if (taskExecutor.getThreadPoolExecutor().getQueue().size() < taskExecutor.getMaxPoolSize() &&
                    taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
                taskExecutor.execute(new ClearOldDataRunnable(
                        similarityToProcessPage.getPageable().getPageNumber() + 1, similarityToProcessList));
                if (similarityToProcessPage.hasNext()
                        && similarityToProcessPage.getPageable().getPageNumber() + 1 < totalPageToProcess) {
                    Pageable page = similarityToProcessPage.nextPageable();
                    log.trace("Requesting similarities to process page {}...", page.getPageNumber() + 1);
                    similarityToProcessPage = userSimilarityService.findSimilarities(profile.getSub(), page);
                    similarityToProcessList = similarityToProcessPage.getContent();
                } else {
                    similarityToProcessList = null;
                }
            } else {
                // If no threads are available, wait for a short period before checking again
                try {
                    Thread.sleep(10); // Sleep for 10ms before checking again
                } catch (InterruptedException e) {
                    log.error("Thread interrupted while waiting for available threads.", e);
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    jobTaskService.fail(jobTask);
                    break; // Exit the loop if interrupted
                }
            }
        }

        // Shutdown the taskExecutor and wait for all tasks to complete
        taskExecutor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after a reasonable time
            taskExecutor.getThreadPoolExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("ClearOldDataJob was interrupted while waiting for termination.", e);
            jobTaskService.fail(jobTask);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        // Complete the task
        if (jobTaskService.isAbandonned(jobTask)) {
            jobTaskService.end(jobTask);
        } else if (similarityToProcessList == null) {
            jobTaskService.complete(jobTask.getId());
            log.info("Old data cleared for Profile {} ({})", profile.getSub(), profile.getId());
        } else {
            jobTaskService.fail(jobTask);
        }
    }

    public ThreadPoolTaskExecutor taskExecutor(int corePoolSize, int maxPoolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.afterPropertiesSet();
        threadPoolTaskExecutor.setThreadNamePrefix("Thread-COD-");
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        log.trace("clearOldDataExecutor created with {} core thread and {} max thread pool",
                threadPoolTaskExecutor.getCorePoolSize(), threadPoolTaskExecutor.getMaxPoolSize());
        return threadPoolTaskExecutor;
    }

    private class ClearOldDataRunnable implements Runnable {
        private final int pageNumber;
        private final List<UserSimilarity> similarityListToProcess;

        ClearOldDataRunnable(int pageNumber, List<UserSimilarity> similarityListToProcess) {
            this.pageNumber = pageNumber;
            this.similarityListToProcess = similarityListToProcess;
        }

        @Override
        public void run() {
            log.debug("Processing page {}...", pageNumber);
            for (UserSimilarity similarity : similarityListToProcess) {
                similarity.setScore(null);
                userSimilarityService.save(similarity);
            }
            try {
                jobTaskService.appendWriteItemCount(jobTask.getId(), similarityListToProcess.size());
                log.trace("Page {} process completed", pageNumber);
            } catch (AnimeRecoException e) {
                log.error("Page {} failed processing items", pageNumber, e);
                hasFailed = true;
            }
        }
    }
}
