package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;

import java.util.List;

public class ProfileDetailsResponse extends ExceptionResponse {

    private String sub;
    private String email;
    private String username;
    private Boolean excludeWatchlistFromRecommendations;
    private List<AnimePojo> excludedRecommendations;

    public ProfileDetailsResponse(String error) {
        super(error);
    }


    public ProfileDetailsResponse() {
        super(null);
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getExcludeWatchlistFromRecommendations() {
        return excludeWatchlistFromRecommendations;
    }

    public void setExcludeWatchlistFromRecommendations(Boolean excludeWatchlistFromRecommendations) {
        this.excludeWatchlistFromRecommendations = excludeWatchlistFromRecommendations;
    }

    public List<AnimePojo> getExcludedRecommendations() {
        return excludedRecommendations;
    }

    public void setExcludedRecommendations(List<AnimePojo> excludedRecommendations) {
        this.excludedRecommendations = excludedRecommendations;
    }
}
