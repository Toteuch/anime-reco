package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobSummaryPojo;
import com.toteuch.anime.reco.domain.job.JobSummaryService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.presentation.controller.response.JobSummariesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JobController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private JobSummaryService jobSummaryService;

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
}
