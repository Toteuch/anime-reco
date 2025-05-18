package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobSummaryPojo;
import com.toteuch.anime.reco.domain.job.JobSummaryService;
import com.toteuch.anime.reco.domain.job.JobTaskService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.presentation.controller.response.ExceptionResponse;
import com.toteuch.anime.reco.presentation.controller.response.JobSummariesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JobController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private JobSummaryService jobSummaryService;
    @Autowired
    private JobTaskService jobTaskService;

    @GetMapping("/job-summary")
    public JobSummariesResponse getJobSummary() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            Map<String, JobSummaryPojo> jobSummaryMap = new HashMap<>();
            JobSummaryPojo processSim = jobSummaryService.getSummary(profile.getSub(), JobName.PROCESS_USER_SIMILARITY);
            JobSummaryPojo processReco = jobSummaryService.getSummary(profile.getSub(),
                    JobName.PROCESS_ANIME_RECOMMENDATION);
            jobSummaryMap.put(JobName.PROCESS_USER_SIMILARITY.name(), processSim);
            jobSummaryMap.put(JobName.PROCESS_ANIME_RECOMMENDATION.name(), processReco);
            return new JobSummariesResponse(jobSummaryMap);
        } else {
            return new JobSummariesResponse("Not authenticated");
        }
    }

    @PostMapping("/job/request/{jobName}")
    public ExceptionResponse requestJobTask(@PathVariable(name = "jobName") String jobName) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            JobName jobJobName = JobName.valueOf(jobName);
            try {
                jobTaskService.create(profile.getSub(), jobJobName, Author.USER);
            } catch (AnimeRecoException e) {
                return new ExceptionResponse(e.getMessage());
            }
            return new ExceptionResponse(null);
        } else {
            return new ExceptionResponse("Not authenticated");
        }
    }

    @PostMapping("/job/abandon/{jobId}")
    public ExceptionResponse abandonJobTask(@PathVariable(name = "jobId") Long jobId) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            try {
                jobTaskService.abandon(profile.getSub(), jobId);
            } catch (AnimeRecoException e) {
                return new ExceptionResponse(e.getMessage());
            }
            return new ExceptionResponse(null);
        } else {
            return new ExceptionResponse("Not authenticated");
        }
    }
}
