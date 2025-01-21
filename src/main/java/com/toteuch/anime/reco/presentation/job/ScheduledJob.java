package com.toteuch.anime.reco.presentation.job;

import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledJob {


    private final RefreshDataFromMalJob refreshDataFromMalJob;

    public ScheduledJob(RefreshDataFromMalJob refreshDataFromMalJob) {
        this.refreshDataFromMalJob = refreshDataFromMalJob;
    }

    @Scheduled(fixedDelay = 100)
    public void start() {
        refreshDataFromMalJob.startSteps();
    }
}
