package com.toteuch.anime.reco.domain.anime.pojo;

import java.util.Date;
import java.util.List;

public class AnimeDetailsPojo {
    Long id;
    String mainPictureMediumUrl;
    String mainTitle;
    List<String> alternativeTitles;
    String mediaTypeLabel;
    Integer numEpisodes;
    Date startDate;
    Date endDate;
    String seasonLabel;
    String synopsis;

    Double animeScore;
    Integer userScore;
    Date lastUpdate;

    List<String> genreLabels;


    // right side
    List<String> pictureLinks;

    // needs to be integrated properly
    Long prequelAnimeId;
    Long sequelAnimeId;

    // low attention
    String statusLabel;

    // buttons
    boolean isAddableToWatchlist;
    boolean isExcludable;
    boolean inWatchlist;
    boolean isExcluded;
    boolean notificationsEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getMediaTypeLabel() {
        return mediaTypeLabel;
    }

    public void setMediaTypeLabel(String mediaTypeLabel) {
        this.mediaTypeLabel = mediaTypeLabel;
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

    public Double getAnimeScore() {
        return animeScore;
    }

    public void setAnimeScore(Double animeScore) {
        this.animeScore = animeScore;
    }

    public Integer getUserScore() {
        return userScore;
    }

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMainPictureMediumUrl() {
        return mainPictureMediumUrl;
    }

    public void setMainPictureMediumUrl(String mainPictureMediumUrl) {
        this.mainPictureMediumUrl = mainPictureMediumUrl;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getGenreLabels() {
        return genreLabels;
    }

    public void setGenreLabels(List<String> genreLabels) {
        this.genreLabels = genreLabels;
    }

    public List<String> getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(List<String> alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public List<String> getPictureLinks() {
        return pictureLinks;
    }

    public void setPictureLinks(List<String> pictureLinks) {
        this.pictureLinks = pictureLinks;
    }

    public boolean isInWatchlist() {
        return inWatchlist;
    }

    public void setInWatchlist(boolean inWatchlist) {
        this.inWatchlist = inWatchlist;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getSeasonLabel() {
        return seasonLabel;
    }

    public void setSeasonLabel(String seasonLabel) {
        this.seasonLabel = seasonLabel;
    }

    public boolean isAddableToWatchlist() {
        return isAddableToWatchlist;
    }

    public void setAddableToWatchlist(boolean addableToWatchlist) {
        isAddableToWatchlist = addableToWatchlist;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public boolean isExcludable() {
        return isExcludable;
    }

    public void setExcludable(boolean excludable) {
        isExcludable = excludable;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public void setExcluded(boolean excluded) {
        isExcluded = excluded;
    }
}
