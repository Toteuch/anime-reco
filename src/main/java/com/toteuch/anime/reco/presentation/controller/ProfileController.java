package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobTaskService;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private MalUserScoreService userScoreService;
    @Autowired
    private JobTaskService jobTaskService;

    @PostMapping("/profile/link/{username}")
    public void linkUser(@PathVariable(name = "username") String username) throws AnimeRecoException {
        DefaultOidcUser user = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileService.findBySub(user.getSubject());
        profileService.linkMalUser(profile.getSub(), username);
    }

    @GetMapping("/scores/{username}")
    public void favorite(@PathVariable(name = "username") String username) {
        userScoreService.refreshUserScores(username);
    }


    @PostMapping("/profile/similarity")
    public void requestUserSimilarityProcess() throws AnimeRecoException {
        DefaultOidcUser user = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileService.findBySub(user.getSubject());
        jobTaskService.create(profile.getSub(), JobName.PROCESS_USER_SIMILARITY, Author.USER);
    }

    @PostMapping("/profile/recommendation")
    public void requestAnimeRecommendationProcess() throws AnimeRecoException {
        DefaultOidcUser user = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileService.findBySub(user.getSubject());
        jobTaskService.create(profile.getSub(), JobName.PROCESS_ANIME_RECOMMENDATION, Author.USER);
    }
}
