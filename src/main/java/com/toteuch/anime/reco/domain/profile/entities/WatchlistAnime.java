package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"profile", "anime"})
})
public class WatchlistAnime {
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

    public WatchlistAnime() {
    }

    public WatchlistAnime(Profile profile, Anime anime) {
        this.profile = profile;
        this.anime = anime;
        this.createdAt = new Date();
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
}
