package com.toteuch.anime.reco.domain.anime.entity;

import jakarta.persistence.*;

@Entity
public class PictureLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String medium;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;

    public PictureLink() {
    }

    public PictureLink(Anime anime, String medium) {
        this.medium = medium;
        this.anime = anime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }
}
