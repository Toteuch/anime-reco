package com.toteuch.anime.reco.presentation.controller.response;

import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;

import java.util.List;

public class AnimeListResultResponse extends ExceptionResponse {

    private List<AnimePojo> animeList;
    private Integer pageNumber;
    private Integer totalPages;

    public AnimeListResultResponse(String error) {
        super(error);
    }

    public AnimeListResultResponse(List<AnimePojo> animeList, Integer pageNumber, Integer totalPages) {
        this.animeList = animeList;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
    }

    public List<AnimePojo> getAnimeList() {
        return animeList;
    }

    public void setAnimeList(List<AnimePojo> animeList) {
        this.animeList = animeList;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
