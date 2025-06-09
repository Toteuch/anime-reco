package com.toteuch.anime.reco.domain.profile.pojo;

import java.util.List;

public class SearchFilterPojo {
    Long id;
    String name;
    List<String> mediaTypes;
    List<String> statusList;
    Integer minSeasonYear;
    Integer maxSeasonYear;
    List<Long> genres;
    List<Long> negativeGenres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public Integer getMinSeasonYear() {
        return minSeasonYear;
    }

    public void setMinSeasonYear(Integer minSeasonYear) {
        this.minSeasonYear = minSeasonYear;
    }

    public Integer getMaxSeasonYear() {
        return maxSeasonYear;
    }

    public void setMaxSeasonYear(Integer maxSeasonYear) {
        this.maxSeasonYear = maxSeasonYear;
    }

    public List<Long> getGenres() {
        return genres;
    }

    public void setGenres(List<Long> genres) {
        this.genres = genres;
    }

    public List<Long> getNegativeGenres() {
        return negativeGenres;
    }

    public void setNegativeGenres(List<Long> negativeGenres) {
        this.negativeGenres = negativeGenres;
    }
}
