package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;

import java.util.List;

public class RecommendationsResponse extends ExceptionResponse {
    List<AnimePojo> animeList;
    String filterLabel;

    public RecommendationsResponse(String error) {
        super(error);
    }

    public RecommendationsResponse(List<AnimePojo> animeList, String filterLabel) {
        this.animeList = animeList;
        this.filterLabel = filterLabel;
    }

    public List<AnimePojo> getAnimeList() {
        return animeList;
    }

    public void setAnimeList(List<AnimePojo> animeList) {
        this.animeList = animeList;
    }

    public String getFilterLabel() {
        return filterLabel;
    }

    public void setFilterLabel(String filterLabel) {
        this.filterLabel = filterLabel;
    }
}