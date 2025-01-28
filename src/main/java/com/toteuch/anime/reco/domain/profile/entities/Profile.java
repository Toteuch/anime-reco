package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String sub;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String avatarUrl;
    @Column(columnDefinition = "DATETIME (3)")
    private Date createdAt;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private MalUser user;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<Favorite> favorites;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<Notification> notifications;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<UserSimilarity> similarities;

    public Profile(String sub, String email, String avatarUrl) {
        this.sub = sub;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = new Date();
    }

    public Profile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public MalUser getUser() {
        return user;
    }

    public void setUser(MalUser user) {
        this.user = user;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<UserSimilarity> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(List<UserSimilarity> similarities) {
        this.similarities = similarities;
    }
}
