package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.job.JobSummaryPojo;

import java.util.Map;

public class JobSummariesResponse extends ExceptionResponse {

    Map<String, JobSummaryPojo> jobSummaryMap;

    public JobSummariesResponse(String error) {
        super(error);
    }

    public JobSummariesResponse(Map<String, JobSummaryPojo> jobSummaryMap) {
        super(null);
        this.jobSummaryMap = jobSummaryMap;
    }

    public Map<String, JobSummaryPojo> getJobSummaryMap() {
        return jobSummaryMap;
    }

    public void setJobSummaryMap(Map<String, JobSummaryPojo> jobSummaryMap) {
        this.jobSummaryMap = jobSummaryMap;
    }
}
