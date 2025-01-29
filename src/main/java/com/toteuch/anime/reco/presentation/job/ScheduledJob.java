package com.toteuch.anime.reco.presentation.job;

import com.toteuch.anime.reco.ApplicationContextHolder;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.ClearOldDataJob;
import com.toteuch.anime.reco.domain.job.JobTaskService;
import com.toteuch.anime.reco.domain.job.ProcessAnimeRecommendationJob;
import com.toteuch.anime.reco.domain.job.ProcessUserSimilarityJob;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledJob {
    private static final Logger log = LoggerFactory.getLogger(ScheduledJob.class);

    private final RefreshDataFromMalJob refreshDataFromMalJob;
    private final JobTaskService jobTaskService;

    public ScheduledJob(RefreshDataFromMalJob refreshDataFromMalJob) {
        this.refreshDataFromMalJob = refreshDataFromMalJob;
        this.jobTaskService = ApplicationContextHolder.getBean(JobTaskService.class);
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void triggerRefresh() {
        log.info("TriggerRefresh...");
        jobTaskService.requestClearOldData(Author.SYSTEM);
    }

    @Scheduled(fixedDelay = 100)
    public void runScheduledTask() {
        JobTask nextJobTask = jobTaskService.getNextQueued();
        if (null != nextJobTask) {
            start(nextJobTask);
        } else {
            if (null != refreshDataFromMalJob) refreshDataFromMalJob.startSteps();
        }
    }

    private void start(JobTask jobTask) {
        try {
            switch (jobTask.getName()) {

                case PROCESS_USER_SIMILARITY:
                    ProcessUserSimilarityJob jobPUS = new ProcessUserSimilarityJob(jobTask);
                    jobPUS.start();
                    break;
                case PROCESS_ANIME_RECOMMENDATION:
                    ProcessAnimeRecommendationJob jobPAR = new ProcessAnimeRecommendationJob(jobTask);
                    jobPAR.start();
                    break;
                case CLEAR_OLD_DATA:
                    ClearOldDataJob jobCOD = new ClearOldDataJob(jobTask);
                    jobCOD.start();
                    break;
                default:
                    log.warn("Job {} not implemented yet", jobTask.getName());
            }
        } catch (Exception e) {
            jobTaskService.fail(jobTask);
            log.error(e.getMessage(), e);
        }
    }
}
