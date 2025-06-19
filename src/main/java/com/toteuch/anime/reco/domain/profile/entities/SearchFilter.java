package com.toteuch.anime.reco.domain.profile.entities;

import com.toteuch.anime.reco.domain.anime.MediaType;
import com.toteuch.anime.reco.domain.anime.Status;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"profile_id", "name"})})
public class SearchFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String title;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    private List<MediaType> mediaTypes;
    private List<Status> statusList;
    private Integer minSeasonYear;
    private Integer maxSeasonYear;
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            uniqueConstraints = @UniqueConstraint(columnNames = {"search_filter_id", "genre_id"}),
            joinColumns = @JoinColumn(name = "search_filter_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "genre_id", nullable = false))
    private List<Genre> genres;
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            uniqueConstraints = @UniqueConstraint(columnNames = {"search_filter_id", "negative_genre_id"}),
            joinColumns = @JoinColumn(name = "search_filter_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "negative_genre_id", nullable = false))
    private List<Genre> negativeGenres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<MediaType> getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(List<MediaType> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }

    public Integer getMinSeasonYear() {
        return minSeasonYear;
    }

    public void setMinSeasonYear(Integer minSeasonYear) {
        this.minSeasonYear = minSeasonYear;
    }

    public Integer getMaxSeasonYear() {
        return maxSeasonYear;
    }

    public void setMaxSeasonYear(Integer maxSeasonYear) {
        this.maxSeasonYear = maxSeasonYear;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getNegativeGenres() {
        return negativeGenres;
    }

    public void setNegativeGenres(List<Genre> negativeGenres) {
        this.negativeGenres = negativeGenres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
