package com.toteuch.anime.reco.domain.anime.entity;

import com.toteuch.anime.reco.domain.anime.AlternativeTitleType;
import jakarta.persistence.*;

@Entity
public class AlternativeTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlternativeTitleType type;
    private String text;

    public AlternativeTitle() {
    }

    public AlternativeTitle(Anime anime, AlternativeTitleType type) {
        this.anime = anime;
        this.type = type;
    }

    public AlternativeTitle(Anime anime, AlternativeTitleType type, String text) {
        this.anime = anime;
        this.type = type;
        this.text = text;
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

    public AlternativeTitleType getType() {
        return type;
    }

    public void setType(AlternativeTitleType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
