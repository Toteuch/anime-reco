package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_profile_anime_recommendation", columnNames = {"profile_id", "anime_id"})
})
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;
    private Double score;
    @Column(columnDefinition = "DATETIME (3)", nullable = false)
    private Date updatedAt;
    private Boolean exclude = false;

    public Recommendation(Profile profile, Anime anime) {
        this.profile = profile;
        this.anime = anime;
        this.updatedAt = new Date();
        this.exclude = false;
    }

    public Recommendation() {
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }
}
