package com.toteuch.anime.reco.domain.anime.entity;

import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.entities.NotificationSetting;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Anime {
    @Id
    private Long id;
    private String title;
    private String mediaType;
    private Integer numEpisodes;
    private Long prequelAnimeId;
    private Long sequelAnimeId;
    private Double score;
    @Column(columnDefinition = "DATETIME (3)")
    private Date detailsUpdate;
    private String mainPictureMediumUrl;
    private String status;
    @Column(columnDefinition = "DATETIME (3)")
    private Date startDate;
    @Column(columnDefinition = "DATETIME (3)")
    private Date endDate;
    private String source;
    private String rating;
    @ManyToMany
    @JoinTable(uniqueConstraints = {
            @UniqueConstraint(name = "unique_anime_genre", columnNames = {"genres_id", "anime_id"})
    }, joinColumns = @JoinColumn(name = "anime_id"))
    private List<Genre> genres;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "anime_id")
    private List<AlternativeTitle> alternativeTitles;
    @ManyToOne
    private Season season;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "anime_id")
    private List<PictureLink> pictureLinks;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "anime_id")
    private List<MalUserScore> userScores;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "anime_id")
    private List<NotificationSetting> notificationSettings;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "anime_id")
    private List<Notification> notifications;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "anime_id")
    private List<Recommendation> recommendations;

    public Anime() {
    }

    public Anime(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Anime(Long id) {
        this.id = id;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getDetailsUpdate() {
        return detailsUpdate;
    }

    public void setDetailsUpdate(Date detailsUpdate) {
        this.detailsUpdate = detailsUpdate;
    }

    public String getMainPictureMediumUrl() {
        return mainPictureMediumUrl;
    }

    public void setMainPictureMediumUrl(String mainPictureMediumUrl) {
        this.mainPictureMediumUrl = mainPictureMediumUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<AlternativeTitle> getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(List<AlternativeTitle> alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public List<PictureLink> getPictureLinks() {
        return pictureLinks;
    }

    public void setPictureLinks(List<PictureLink> pictureLinks) {
        this.pictureLinks = pictureLinks;
    }

    public List<MalUserScore> getUserScores() {
        return userScores;
    }

    public void setUserScores(List<MalUserScore> userScores) {
        this.userScores = userScores;
    }

    public List<NotificationSetting> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(List<NotificationSetting> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
