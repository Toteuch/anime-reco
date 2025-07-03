package com.toteuch.anime.reco.domain.job;

import com.toteuch.anime.reco.domain.job.entities.JobTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class JobSummaryService {
    private static final Logger log = LoggerFactory.getLogger(JobSummaryService.class);

    @Autowired
    private JobTaskService jobTaskService;

    public JobSummaryPojo getSummary(String sub, JobName jobName) {
        JobTask lastOccurrence = jobTaskService.getLastOccurrence(sub, jobName, null);
        JobSummaryPojo pojo = mapJobTask(lastOccurrence);
        if (pojo == null) {
            pojo = new JobSummaryPojo();
            pojo.setStatus("N/A");
            pojo.setEndDate("N/A");
            pojo.setEstimatedEndDate("N/A");
            pojo.setFormattedExecutionTime("N/A");
            pojo.setReadItemCount(0L);
            pojo.setProcessedItemCount(0L);
            pojo.setCanBeStarted(true);
            pojo.setCanBeAbandoned(false);
        }
        pojo.setJobName(jobName.name());
        pojo.setJobLabel(jobName.getLabel());
        return pojo;
    }

    private JobSummaryPojo mapJobTask(JobTask jobTask) {
        if (jobTask == null) return null;
        JobSummaryPojo pojo = new JobSummaryPojo();
        pojo.setStatus(jobTask.getStatus().name());
        pojo.setJobId(jobTask.getId());
        Long executionTime = null;
        if (jobTask.getStartedAt() != null && jobTask.getUpdatedAt() != null) {
            LocalDateTime localStartedAt =
                    jobTask.getStartedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime localUpdatedAt =
                    jobTask.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            executionTime = SECONDS.between(localStartedAt, localUpdatedAt);
            if (executionTime != 0L) {
                int seconds = (int) (executionTime % 60);
                int minutes = (int) (executionTime / 60 % 60);
                pojo.setFormattedExecutionTime(minutes + "m" + (seconds < 10 ? "0" + seconds : seconds) + "s");
            }
        }
        pojo.setReadItemCount(jobTask.getReadItemCount() != null ? jobTask.getReadItemCount() : 0L);
        pojo.setProcessedItemCount(jobTask.getProcessItemCount() != null ? jobTask.getProcessItemCount() : 0L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        switch (jobTask.getStatus()) {
            case ABANDONED:
                pojo.setEndDate(sdf.format(jobTask.getUpdatedAt()));
            case COMPLETED, FAILED:
                pojo.setCanBeAbandoned(false);
                pojo.setCanBeStarted(true);
                if (pojo.getEndDate() == null)
                    pojo.setEndDate(sdf.format(jobTask.getEndedAt()));
                if (executionTime == null || executionTime == 0L) {
                    pojo.setFormattedExecutionTime("N/A");
                }
                break;
            case STARTED:
                pojo.setCanBeAbandoned(true);
                pojo.setCanBeStarted(false);
                if (executionTime == null || executionTime == 0L) {
                    pojo.setFormattedExecutionTime("Starting up...");
                    pojo.setEstimatedEndDate("Calculating...");
                }
                if (executionTime != null && executionTime != 0L
                        && jobTask.getReadItemCount() != null && jobTask.getProcessItemCount() != null) {
                    Double secondsPerItem = ((double) executionTime / (double) jobTask.getProcessItemCount());
                    int estimatedDurationSeconds = (int) (jobTask.getReadItemCount() * secondsPerItem);
                    Date estimatedEndDate =
                            new Date(jobTask.getStartedAt().getTime() + estimatedDurationSeconds * 1000L);
                    pojo.setEstimatedEndDate(sdf.format(estimatedEndDate));
                }
                break;
            case QUEUED:
                pojo.setCanBeAbandoned(true);
                pojo.setCanBeStarted(false);
                pojo.setEstimatedEndDate("Not started yet...");
                pojo.setFormattedExecutionTime("Not started yet...");
                break;
        }
        return pojo;
    }
}
