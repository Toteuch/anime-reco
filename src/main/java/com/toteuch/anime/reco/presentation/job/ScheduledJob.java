package com.toteuch.anime.reco.presentation.job;

import com.toteuch.anime.reco.ApplicationContextHolder;
import com.toteuch.anime.reco.domain.job.JobTaskService;
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
                    ProcessUserSimilarityJob job = new ProcessUserSimilarityJob(jobTask);
                    job.start();
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
