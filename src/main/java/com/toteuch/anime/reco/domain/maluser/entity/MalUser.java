package com.toteuch.anime.reco.domain.maluser.entity;

import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_username", columnNames = "username")
})
public class MalUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Date lastSeen;
    private Date lastUpdate;
    private int animeListSize;
    private int animeRatedCount;
    @Column(columnDefinition = "tinyint(1)")
    private boolean isListVisible = true;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<MalUserScore> scores;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<UserSimilarity> similarities;

    public MalUser() {
    }

    public MalUser(String username) {
        this.username = username;
        this.lastSeen = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getAnimeListSize() {
        return animeListSize;
    }

    public void setAnimeListSize(int animeListSize) {
        this.animeListSize = animeListSize;
    }

    public int getAnimeRatedCount() {
        return animeRatedCount;
    }

    public void setAnimeRatedCount(int animeRatedCount) {
        this.animeRatedCount = animeRatedCount;
    }

    public boolean isListVisible() {
        return isListVisible;
    }

    public void setListVisible(boolean listVisible) {
        isListVisible = listVisible;
    }

    public List<MalUserScore> getScores() {
        return scores;
    }

    public void setScores(List<MalUserScore> scores) {
        this.scores = scores;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<UserSimilarity> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(List<UserSimilarity> similarities) {
        this.similarities = similarities;
    }
}
