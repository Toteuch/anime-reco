package com.toteuch.anime.reco.data.api;

import java.util.List;
import java.util.Map;

public class AnimeDetailsRaw {
    private Long id;
    private String title;
    private String mediaType;
    private Map<Long, String> genres;
    private String status;
    private Double score;
    private Integer numEpisodes;
    private Long prequelAnimeId;
    private Long sequelAnimeId;
    private String mainPictureUrlMedium;
    private Map<String, Object> alternativeTitles;
    private String startDate;
    private String endDate;
    private Integer startSeasonYear;
    private String startSeasonSeason;
    private String source;
    private String rating;
    private List<String> pictureUrlsMedium;
    private String background;
    private String synopsis;
    private String createdAt;
    private String updatedAt;
    private String nsfw;

    public AnimeDetailsRaw() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Map<Long, String> getGenres() {
        return genres;
    }

    public void setGenres(Map<Long, String> genres) {
        this.genres = genres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getNumEpisodes() {
        return numEpisodes;
    }

    public void setNumEpisodes(Integer numEpisodes) {
        this.numEpisodes = numEpisodes;
    }

    public Long getPrequelAnimeId() {
        return prequelAnimeId;
    }

    public void setPrequelAnimeId(Long prequelAnimeId) {
        this.prequelAnimeId = prequelAnimeId;
    }

    public Long getSequelAnimeId() {
        return sequelAnimeId;
    }

    public void setSequelAnimeId(Long sequelAnimeId) {
        this.sequelAnimeId = sequelAnimeId;
    }

    public String getMainPictureUrlMedium() {
        return mainPictureUrlMedium;
    }

    public void setMainPictureUrlMedium(String mainPictureUrlMedium) {
        this.mainPictureUrlMedium = mainPictureUrlMedium;
    }

    public Map<String, Object> getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(Map<String, Object> alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getStartSeasonYear() {
        return startSeasonYear;
    }

    public void setStartSeasonYear(Integer startSeasonYear) {
        this.startSeasonYear = startSeasonYear;
    }

    public String getStartSeasonSeason() {
        return startSeasonSeason;
    }

    public void setStartSeasonSeason(String startSeasonSeason) {
        this.startSeasonSeason = startSeasonSeason;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<String> getPictureUrlsMedium() {
        return pictureUrlsMedium;
    }

    public void setPictureUrlsMedium(List<String> pictureUrlsMedium) {
        this.pictureUrlsMedium = pictureUrlsMedium;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNsfw() {
        return nsfw;
    }

    public void setNsfw(String nsfw) {
        this.nsfw = nsfw;
    }
}