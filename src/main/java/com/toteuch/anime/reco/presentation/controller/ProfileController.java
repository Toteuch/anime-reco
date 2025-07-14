package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.entity.AlternativeTitle;
import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;
import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobTaskService;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.RecommendationService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import com.toteuch.anime.reco.presentation.controller.response.ProfileDetailsResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private MalUserScoreService userScoreService;
    @Autowired
    private JobTaskService jobTaskService;
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/profile/details")
    public ProfileDetailsResponse getProfileDetails() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            List<Recommendation> excludedRecommendations = recommendationService.getExcludedRecommendations(profile);
            return getProfileDetailResponse(profile, excludedRecommendations);
        } else {
            return new ProfileDetailsResponse("Not authenticated");
        }
    }

    @PostMapping("/profile/link/{username}")
    public ProfileDetailsResponse linkUser(@PathVariable(name = "username") String username) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            try {
                profile = profileService.linkMalUser(profile.getSub(), username);
                userScoreService.refreshUserScores(username);
                List<Recommendation> excludedRecommendations = recommendationService.getExcludedRecommendations(profile);
                return getProfileDetailResponse(profile, excludedRecommendations);
            } catch (AnimeRecoException e) {
                return new ProfileDetailsResponse(e.getMessage());
            }
        } else {
            return new ProfileDetailsResponse("Not authenticated");
        }
    }

    @PutMapping("/profile/exclude-watchlist-from-recommendations")
    public ProfileDetailsResponse updateExcludeWatchlistFromRecommendations(@PathParam(
            "excludeWatchlistFromRecommendations") Boolean excludeWatchlistFromRecommendations) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            List<Recommendation> excludedRecommendations = recommendationService.getExcludedRecommendations(profile);
            return getProfileDetailResponse(profileService.setExcludeWatchlistFromRecommendations(profile,
                    excludeWatchlistFromRecommendations), excludedRecommendations);
        } else {
            return new ProfileDetailsResponse("Not authenticated");
        }
    }

    private ProfileDetailsResponse getProfileDetailResponse(Profile profile,
                                                            List<Recommendation> excludedRecommendations) {
        ProfileDetailsResponse response = new ProfileDetailsResponse();
        response.setSub(profile.getSub());
        response.setEmail(profile.getEmail());
        response.setUsername(profile.getUser() != null ? profile.getUser().getUsername() : "");
        response.setExcludeWatchlistFromRecommendations(profile.getExcludeWatchListFromRecommendation());
        response.setUserListRestricted(profile.getUser() != null && !profile.getUser().isListVisible());
        List<AnimePojo> animePojos = new ArrayList<>();
        if (excludedRecommendations != null && !excludedRecommendations.isEmpty()) {
            for (Recommendation reco : excludedRecommendations) {
                AnimePojo pojo = new AnimePojo();
                pojo.setId(reco.getAnime().getId());
                pojo.setTitle(reco.getAnime().getTitle());
                pojo.setMainMediumUrl(reco.getAnime().getMainPictureMediumUrl());
                List<String> altTitles = new ArrayList<>();
                for (AlternativeTitle altTitle : reco.getAnime().getAlternativeTitles()) {
                    altTitles.add(altTitle.getText());
                }
                pojo.setAltTitles(altTitles);
                animePojos.add(pojo);
            }
        }
        response.setExcludedRecommendations(animePojos);
        return response;
    }

    /*
        OLD
     */


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
