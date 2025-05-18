package com.toteuch.anime.reco.domain.job;

public class JobSummaryPojo {
    private String jobName;
    private String jobLabel;
    private Long jobId;
    private Long processedItemCount;
    private Long readItemCount;
    private String status;
    private String formattedExecutionTime;
    private String estimatedEndDate;
    private String endDate;
    private boolean canBeStarted;
    private boolean canBeAbandoned;

    public JobSummaryPojo() {
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getProcessedItemCount() {
        return processedItemCount;
    }

    public void setProcessedItemCount(Long processedItemCount) {
        this.processedItemCount = processedItemCount;
    }

    public Long getReadItemCount() {
        return readItemCount;
    }

    public void setReadItemCount(Long readItemCount) {
        this.readItemCount = readItemCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedExecutionTime() {
        return formattedExecutionTime;
    }

    public void setFormattedExecutionTime(String formattedExecutionTime) {
        this.formattedExecutionTime = formattedExecutionTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isCanBeStarted() {
        return canBeStarted;
    }

    public void setCanBeStarted(boolean canBeStarted) {
        this.canBeStarted = canBeStarted;
    }

    public boolean isCanBeAbandoned() {
        return canBeAbandoned;
    }

    public void setCanBeAbandoned(boolean canBeAbandoned) {
        this.canBeAbandoned = canBeAbandoned;
    }

    public String getEstimatedEndDate() {
        return estimatedEndDate;
    }

    public void setEstimatedEndDate(String estimatedEndDate) {
        this.estimatedEndDate = estimatedEndDate;
    }

    public String getJobLabel() {
        return jobLabel;
    }

    public void setJobLabel(String jobLabel) {
        this.jobLabel = jobLabel;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
