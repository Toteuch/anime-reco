package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_anime_profile", columnNames = {"anime_id", "profile_id"})
})
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;
    @Column(columnDefinition = "DATETIME (3)", nullable = false)
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Author author;

    public NotificationSetting(Profile profile, Anime anime, Author author) {
        this.anime = anime;
        this.profile = profile;
        this.author = author;
        this.createdAt = new Date();
    }

    public NotificationSetting() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
