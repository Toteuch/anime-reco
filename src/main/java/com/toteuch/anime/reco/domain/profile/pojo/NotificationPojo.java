package com.toteuch.anime.reco.domain.profile.pojo;

import com.toteuch.anime.reco.domain.profile.NotificationType;

import java.util.Date;

public class NotificationPojo {
    Long id;
    NotificationType type;
    String animeTitle;
    Date createdAt;
    Date readAt;
    String mainMediumUrl;
    Long animeId;
    String tag;

    public NotificationPojo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public String getMainMediumUrl() {
        return mainMediumUrl;
    }

    public void setMainMediumUrl(String mainMediumUrl) {
        this.mainMediumUrl = mainMediumUrl;
    }

    public Long getAnimeId() {
        return animeId;
    }

    public void setAnimeId(Long animeId) {
        this.animeId = animeId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
