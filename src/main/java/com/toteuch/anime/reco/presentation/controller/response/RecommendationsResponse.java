package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.profile.pojo.RecommendationPojo;

import java.util.List;

public class RecommendationsResponse extends ExceptionResponse {
    List<RecommendationPojo> recoList;

    public RecommendationsResponse(String error) {
        super(error);
    }

    public RecommendationsResponse(List<RecommendationPojo> recoList) {
        this.recoList = recoList;
    }

    public List<RecommendationPojo> getRecoList() {
        return recoList;
    }

    public void setRecoList(List<RecommendationPojo> recoList) {
        this.recoList = recoList;
    }
}