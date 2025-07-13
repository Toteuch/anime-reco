package com.toteuch.anime.reco.data.api;

import java.io.Serializable;

public class UserAnimeScoreRaw implements Serializable {
    private String username;
    private Long animeId;
    private String animeTitle;
    private Double userScore;
    private String updatedAt;

    public UserAnimeScoreRaw(String username, Long animeId, String animeTitle, Double userScore, String updatedAt) {
        this.username = username;
        this.animeId = animeId;
        this.animeTitle = animeTitle;
        this.userScore = userScore;
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAnimeId() {
        return animeId;
    }

    public void setAnimeId(Long animeId) {
        this.animeId = animeId;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public Double getUserScore() {
        return userScore;
    }

    public void setUserScore(Double userScore) {
        this.userScore = userScore;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}