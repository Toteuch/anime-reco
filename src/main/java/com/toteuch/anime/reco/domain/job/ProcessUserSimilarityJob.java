package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.ApplicationContextHolder;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.maluser.MalUserService;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.UserSimilarityService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProcessUserSimilarityJob {
    private static final Logger log = LoggerFactory.getLogger(ProcessUserSimilarityJob.class);
    private final JobTaskService jobTaskService;
    private final MalUserService userService;
    private final UserSimilarityService userSimilarityService;
    private final MalUserScoreService userScoreService;
    private final EntityManager entityManager;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final int pageSize;
    private final int clearCacheInterval;

    private JobTask jobTask;
    private Profile profile;
    private Map<Long, Double> referenceUserMeanCenteredScores;
    private boolean hasFailed = false;

    public ProcessUserSimilarityJob(JobTask jobTask) {
        this.jobTask = jobTask;
        int corePoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-user-similarity-job.core-pool-size"));
        int maxPoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-user-similarity-job.max-pool-size"));
        this.pageSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-user-similarity-job.chunk-size"));
        this.clearCacheInterval = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-user-similarity-job.clear-cache-interval"));
        this.taskExecutor = taskExecutor(corePoolSize, maxPoolSize);
        this.userService = ApplicationContextHolder.getBean(MalUserService.class);
        this.jobTaskService = ApplicationContextHolder.getBean(JobTaskService.class);
        this.userSimilarityService = ApplicationContextHolder.getBean(UserSimilarityService.class);
        this.userScoreService = ApplicationContextHolder.getBean(MalUserScoreService.class);
        this.entityManager = ApplicationContextHolder.getBean(EntityManager.class);
    }

    public void start() throws AnimeRecoException {
        jobTask = jobTaskService.start(jobTask.getId());
        profile = jobTask.getProfile();
        log.debug("Gathering data to start processing user similarities for Profile {} ({})...",
                profile.getSub(), profile.getId());
        Date startDate = jobTask.getStartedAt();

        // READER
        Page<MalUser> userToProcessPage = userService.getUserListToProcess(profile.getId(), startDate,
                PageRequest.of(0, pageSize, Sort.by("id").ascending()));
        log.debug("{} similarities to process", userToProcessPage.getTotalElements());
        jobTask = jobTaskService.setReadItemCount(jobTask, userToProcessPage.getTotalElements());

        // PRE-PROCESS REF DATA
        List<MalUserScore> referenceUserMUSList = userScoreService.getByUser(profile.getUser());
        referenceUserMeanCenteredScores =
                userSimilarityService.meanCenterAnimeScoreList(referenceUserMUSList);
        log.info("Starting to process similarities for Profile {} ({})...", profile.getSub(), profile.getId());

        List<MalUser> userToProcessList = userToProcessPage.getContent();
        log.debug("Number of pages to process {}", userToProcessPage.getTotalPages());
        while (userToProcessList != null && !hasFailed && !jobTaskService.isAbanonned(jobTask)) {
            if (taskExecutor.getThreadPoolExecutor().getQueue().size() < taskExecutor.getMaxPoolSize() &&
                    taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
                // asynchronous processors -> update the task
                taskExecutor.execute(new ProcessUserSimilarityRunnable(
                        userToProcessPage.getPageable().getPageNumber() + 1, userToProcessList));
                if (userToProcessPage.hasNext()) {
                    Pageable page = userToProcessPage.nextPageable();
                    log.trace("Requesting users to process page {}...", page.getPageNumber() + 1);
                    userToProcessPage = userService.getUserListToProcess(profile.getId(), startDate, page);
                    userToProcessList = userToProcessPage.getContent();
                    if (page.getPageNumber() % clearCacheInterval == 0) {
                        log.debug("Clearing entityManager cache...");
                        entityManager.clear();
                    }
                } else {
                    userToProcessList = null;
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
            log.error("ProcessUserSimilarityJob was interrupted while waiting for termination.", e);
            jobTaskService.fail(jobTask);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        // Complete the task
        if (jobTaskService.isAbanonned(jobTask)) {
            jobTaskService.end(jobTask);
        } else if (userToProcessList == null) {
            jobTaskService.complete(jobTask.getId());
            log.info("Similarities processed for Profile {} ({})", profile.getSub(), profile.getId());
        } else {
            jobTaskService.fail(jobTask);
        }
    }

    public ThreadPoolTaskExecutor taskExecutor(int corePoolSize, int maxPoolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.afterPropertiesSet();
        threadPoolTaskExecutor.setThreadNamePrefix("Thread-US-");
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        log.trace("processUserSimilarityTaskExecutor created with {} core thread and {} max thread pool", threadPoolTaskExecutor.getCorePoolSize(), threadPoolTaskExecutor.getMaxPoolSize());
        return threadPoolTaskExecutor;
    }

    private class ProcessUserSimilarityRunnable implements Runnable {
        private final int pageNumber;
        private final List<MalUser> userListToProcess;

        ProcessUserSimilarityRunnable(int pageNumber, List<MalUser> userListToProcess) {
            this.pageNumber = pageNumber;
            this.userListToProcess = userListToProcess;
        }

        @Override
        public void run() {
            log.debug("Processing page {}...", pageNumber);
            for (MalUser userToProcess : userListToProcess) {
                log.trace("Processing similarity between Profile {} ({}) and User {}", profile.getSub(),
                        profile.getId(), userToProcess.getUsername());
                Double similarity = userSimilarityService.processSimilarity(userToProcess, referenceUserMeanCenteredScores);
                log.trace("Similarity between Profile {} ({}) and User {} is {}", profile.getSub(),
                        profile.getId(), userToProcess.getUsername(), similarity);
                UserSimilarity userSimilarity = userSimilarityService.getUserSimilarity(profile, userToProcess);
                if (userSimilarity == null) {
                    userSimilarity = new UserSimilarity();
                }
                userSimilarity.setProfile(profile);
                userSimilarity.setUser(userToProcess);
                userSimilarity.setScore(similarity);
                userSimilarity.setLastUpdate(new Date());
                userSimilarityService.save(userSimilarity);
            }
            try {
                jobTaskService.appendWriteItemCount(jobTask.getId(), userListToProcess.size());
                log.trace("Page {} process completed", pageNumber);
            } catch (AnimeRecoException e) {
                log.error("Page {} failed processing items", pageNumber, e);
                hasFailed = true;
            }
        }
    }
}
