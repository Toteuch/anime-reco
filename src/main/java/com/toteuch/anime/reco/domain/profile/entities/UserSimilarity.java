package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_profile_user_similarity", columnNames = {"profile_id", "user_id"})
})
public class UserSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private MalUser user;
    private Double score;
    private Date lastUpdate;

    public UserSimilarity(Profile profile, MalUser user, Double score) {
        this.profile = profile;
        this.user = user;
        this.score = score;
        this.lastUpdate = new Date();
    }

    public UserSimilarity() {
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

    public MalUser getUser() {
        return user;
    }

    public void setUser(MalUser user) {
        this.user = user;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
