package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.NotificationType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;
    @Column(columnDefinition = "DATETIME (3)", nullable = false)
    private Date createdAt;
    @Column(columnDefinition = "DATETIME (3)")
    private Date readAt;

    public Notification(Profile profile, NotificationType type, Anime anime) {
        this.profile = profile;
        this.type = type;
        this.anime = anime;
        this.createdAt = new Date();
    }

    public Notification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
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
}
