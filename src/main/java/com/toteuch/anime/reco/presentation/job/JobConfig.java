package com.toteuch.anime.reco.presentation.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class JobConfig {

    @Autowired
    RefreshDataFromMalJob refreshDataFromMalJob;

    @Bean
    @ConditionalOnProperty(value = "app.presentation.scheduled.enabled", matchIfMissing = true, havingValue = "true")
    public ScheduledJob scheduledJob() {
        return new ScheduledJob(refreshDataFromMalJob);
    }
}
