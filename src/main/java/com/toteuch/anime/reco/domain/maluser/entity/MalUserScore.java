package com.toteuch.anime.reco.domain.maluser.entity;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_anime", columnNames = {"user_id", "anime_id"})
})
public class MalUserScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private MalUser user;
    @ManyToOne
    @JoinColumn(name = "anime_id")
    private Anime anime;
    @Column(nullable = false)
    private Double score;

    public MalUserScore(MalUser user, Anime anime) {
        this.user = user;
        this.anime = anime;
    }

    public MalUserScore() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MalUser getUser() {
        return user;
    }

    public void setUser(MalUser user) {
        this.user = user;
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
}
