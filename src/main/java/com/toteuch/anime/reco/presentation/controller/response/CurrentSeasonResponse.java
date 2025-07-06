package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;

import java.util.List;

public class CurrentSeasonResponse extends ExceptionResponse {
    List<AnimePojo> animeList;
    String seasonLabel;
    Integer totalPages;
    Integer pageNumber;

    public CurrentSeasonResponse(String error) {
        super(error);
    }

    public CurrentSeasonResponse(List<AnimePojo> animeList, String seasonLabel, Integer totalPages, Integer pageNumber) {
        this.animeList = animeList;
        this.seasonLabel = seasonLabel;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
    }

    public List<AnimePojo> getAnimeList() {
        return animeList;
    }

    public void setAnimeList(List<AnimePojo> animeList) {
        this.animeList = animeList;
    }

    public String getSeasonLabel() {
        return seasonLabel;
    }

    public void setSeasonLabel(String seasonLabel) {
        this.seasonLabel = seasonLabel;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
