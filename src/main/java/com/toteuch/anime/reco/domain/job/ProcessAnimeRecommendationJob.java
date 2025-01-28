package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.ApplicationContextHolder;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.profile.RecommendationService;
import com.toteuch.anime.reco.domain.profile.UserSimilarityService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProcessAnimeRecommendationJob {
    private static final Logger log = LoggerFactory.getLogger(ProcessAnimeRecommendationJob.class);
    private final JobTaskService jobTaskService;
    private final AnimeService animeService;
    private final UserSimilarityService userSimilarityService;
    private final MalUserScoreService userScoreService;
    private final RecommendationService recommendationService;
    private final EntityManager entityManager;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final int pageSize;
    private final int relevantUserCount;
    private boolean hasFailed = false;
    private Profile profile;
    private JobTask jobTask;
    private Map<MalUser, Double> relevantUserMap;
    private Map<MalUser, Map<Long, Double>> usersScores;

    public ProcessAnimeRecommendationJob(JobTask jobTask) {
        this.jobTask = jobTask;
        int corePoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-anime-recommendation-job.core-pool-size"));
        int maxPoolSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-anime-recommendation-job.max-pool-size"));
        this.pageSize = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-anime-recommendation-job.chunk-size"));
        this.relevantUserCount = Integer.parseInt((String) ApplicationContextHolder.getProperty(
                "app.domain.job.process-anime-recommendation-job.relevant-user-count"));
        this.taskExecutor = taskExecutor(corePoolSize, maxPoolSize);
        this.jobTaskService = ApplicationContextHolder.getBean(JobTaskService.class);
        this.animeService = ApplicationContextHolder.getBean(AnimeService.class);
        this.userSimilarityService = ApplicationContextHolder.getBean(UserSimilarityService.class);
        this.userScoreService = ApplicationContextHolder.getBean(MalUserScoreService.class);
        this.recommendationService = ApplicationContextHolder.getBean(RecommendationService.class);
        this.entityManager = ApplicationContextHolder.getBean(EntityManager.class);
    }

    public void start() throws AnimeRecoException {
        jobTask = jobTaskService.start(jobTask.getId());
        profile = jobTask.getProfile();
        log.debug("Gathering data to start processing recommendations for Profile {} ({})...",
                profile.getSub(), profile.getId());

        // READER
        Page<Anime> animeToProcessPage = animeService.getAnimeListToProcess(
                PageRequest.of(0, pageSize, Sort.by("id").ascending()));
        log.debug("{} recommendations to process", animeToProcessPage.getTotalElements());
        jobTask = jobTaskService.setReadItemCount(jobTask, animeToProcessPage.getTotalElements());

        // PRE-PROCESS REF DATA
        List<MalUser> relevantUsers = userSimilarityService.getMostSimilarUsers(profile.getSub(),
                Limit.of(relevantUserCount));
        relevantUserMap = new HashMap<>();
        usersScores = new HashMap<>();
        for (MalUser relevantUser : relevantUsers) {
            Map<Long, Double> userScores = new HashMap<>();
            relevantUserMap.put(relevantUser,
                    userSimilarityService.getUserSimilarity(profile, relevantUser).getScore());
            userScoreService.getByUser(relevantUser).forEach(userScore ->
                    userScores.put(userScore.getAnime().getId(), userScore.getScore()));
            usersScores.put(relevantUser, userScores);
        }
        log.info("Starting to process recommendations for Profile {} ({})...", profile.getSub(), profile.getId());
        List<Anime> animeToProcessList = animeToProcessPage.getContent();
        log.debug("Number of pages to process {}", animeToProcessPage.getTotalPages());
        while (animeToProcessList != null && !hasFailed && !jobTaskService.isAbanonned(jobTask)) {
            if (taskExecutor.getThreadPoolExecutor().getQueue().size() < taskExecutor.getMaxPoolSize() &&
                    taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
                taskExecutor.execute(new ProcessAnimeRecommendationRunnable(
                        animeToProcessPage.getPageable().getPageNumber() + 1, animeToProcessList));
                if (animeToProcessPage.hasNext()) {
                    Pageable page = animeToProcessPage.nextPageable();
                    log.trace("Requesting anime to process page {}...", page.getPageNumber() + 1);
                    animeToProcessPage = animeService.getAnimeListToProcess(page);
                    animeToProcessList = animeToProcessPage.getContent();
                } else {
                    animeToProcessList = null;
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
            log.error("ProcessAnimeRecommendationJob was interrupted while waiting for termination.", e);
            jobTaskService.fail(jobTask);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        // Complete the task
        if (jobTaskService.isAbanonned(jobTask)) {
            jobTaskService.end(jobTask);
        } else if (animeToProcessList == null) {
            jobTaskService.complete(jobTask.getId());
            log.info("Similarities processed for Profile {} ({})", profile.getSub(), profile.getId());
        } else {
            jobTaskService.fail(jobTask);
        }
    }

    public ThreadPoolTaskExecutor taskExecutor(int corePoolSize, int maxPoolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.afterPropertiesSet();
        threadPoolTaskExecutor.setThreadNamePrefix("Thread-AR-");
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        log.trace("processAnimeRecommendationTaskExecutor created with {} core thread and {} max thread pool",
                threadPoolTaskExecutor.getCorePoolSize(), threadPoolTaskExecutor.getMaxPoolSize());
        return threadPoolTaskExecutor;
    }

    private class ProcessAnimeRecommendationRunnable implements Runnable {

        private final int pageNumber;
        private final List<Anime> animeListToProcess;

        ProcessAnimeRecommendationRunnable(int pageNumber, List<Anime> animeListToProcess) {
            this.pageNumber = pageNumber;
            this.animeListToProcess = animeListToProcess;
        }

        @Override
        public void run() {
            log.debug("Processing page {}...", pageNumber);
            for (Anime animeToProcess : animeListToProcess) {
                log.trace("Processing recommendation of anime {}({}) for Profile {}({})",
                        animeToProcess.getTitle(), animeToProcess.getId(), profile.getSub(), profile.getId());
                Double score = recommendationService.processRecommendationRate(relevantUserMap,
                        animeToProcess, usersScores);
                log.trace("Recommendation of anime {}({}) for Profile {}({}) is {}",
                        animeToProcess.getTitle(), animeToProcess.getId(), profile.getSub(), profile.getId(), score);
                Recommendation reco = recommendationService.getRecommendation(profile, animeToProcess);
                if (reco == null) {
                    reco = new Recommendation(profile, animeToProcess);
                }
                reco.setScore(score);
                reco.setUpdatedAt(new Date());
                recommendationService.save(reco);
            }
            try {
                jobTaskService.appendWriteItemCount(jobTask.getId(), animeListToProcess.size());
                log.trace("Page {} process completed", pageNumber);
            } catch (AnimeRecoException e) {
                log.error("Page {} failed processing items", pageNumber, e);
                hasFailed = true;
            }
        }
    }
}
