package com.toteuch.anime.reco.data.api.response.animedetails;

public class StartSeasonResponse {
    private Integer year;
    private String season;

    public StartSeasonResponse() {
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
