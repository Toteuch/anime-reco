package com.toteuch.anime.reco.data.api.response.animedetails;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimeDetailsResponse {
    private Long id;
    private String title;
    @JsonProperty("media_type")
    private String mediaType;
    private GenreResponse[] genres;
    private String status;
    private Double mean;
    @JsonProperty("num_episodes")
    private Integer numEpisodes;
    @JsonProperty("related_anime")
    private RelatedAnimeResponse[] relatedAnimeResponses;
    @JsonProperty("main_picture")
    private MainPictureResponse mainPicture;
    @JsonProperty("alternative_titles")
    private AlternativeTitlesResponse alternativeTitles;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("start_season")
    private StartSeasonResponse startSeason;
    private String source;
    private String rating;
    private PictureResponse[] pictures;
    private String background;
    private String synopsis;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private String nsfw;

    public AnimeDetailsResponse() {
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

    public GenreResponse[] getGenres() {
        return genres;
    }

    public void setGenres(GenreResponse[] genres) {
        this.genres = genres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Integer getNumEpisodes() {
        return numEpisodes;
    }

    public void setNumEpisodes(Integer numEpisodes) {
        this.numEpisodes = numEpisodes;
    }

    public RelatedAnimeResponse[] getRelatedAnimes() {
        return relatedAnimeResponses;
    }

    public void setRelatedAnimes(RelatedAnimeResponse[] relatedAnimeResponses) {
        this.relatedAnimeResponses = relatedAnimeResponses;
    }

    public RelatedAnimeResponse[] getRelatedAnimeResponses() {
        return relatedAnimeResponses;
    }

    public void setRelatedAnimeResponses(RelatedAnimeResponse[] relatedAnimeResponses) {
        this.relatedAnimeResponses = relatedAnimeResponses;
    }

    public MainPictureResponse getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(MainPictureResponse mainPicture) {
        this.mainPicture = mainPicture;
    }

    public AlternativeTitlesResponse getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(AlternativeTitlesResponse alternativeTitles) {
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

    public StartSeasonResponse getStartSeason() {
        return startSeason;
    }

    public void setStartSeason(StartSeasonResponse startSeason) {
        this.startSeason = startSeason;
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

    public PictureResponse[] getPictures() {
        return pictures;
    }

    public void setPictures(PictureResponse[] pictures) {
        this.pictures = pictures;
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
