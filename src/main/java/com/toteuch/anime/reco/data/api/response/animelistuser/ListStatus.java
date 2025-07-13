package com.toteuch.anime.reco.data.api.response.animelistuser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListStatus {
    private Double score;

    @JsonProperty("updated_at")
    private String updatedAt;

    public ListStatus() {
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
