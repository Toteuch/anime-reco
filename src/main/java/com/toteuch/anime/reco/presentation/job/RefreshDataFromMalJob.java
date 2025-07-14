package com.toteuch.anime.reco.presentation.job;

import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.maluser.MalUserService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefreshDataFromMalJob {
    private static final Logger log = LoggerFactory.getLogger(RefreshDataFromMalJob.class);

    @Autowired
    private MalUserService userService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private MalUserScoreService userScoreService;
    @Autowired
    private ProfileService profileService;

    public void startSteps() {
        log.debug("Starting RefreshDataFromMalJob...");
        userService.scrapLastSeenUsers();
        userScoreService.collectUserScores();
        userScoreService.refreshUpdatedUserScores();
        userScoreService.refreshOldUserScores();
        animeService.collectAnimeDetails();
        animeService.refreshOldAnimeDetails();
        profileService.refreshLinkedUserLists();
        log.debug("RefreshDataFromMalJob completed");
    }

}
