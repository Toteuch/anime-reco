package com.toteuch.anime.reco.domain.anime.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "name_unique", columnNames = "name")
})
public class Genre {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;

    public Genre() {
    }

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Long> getGenresIds(List<Genre> genres) {
        List<Long> genresIds = new ArrayList<>();
        genres.forEach(g -> genresIds.add(g.getId()));
        return genresIds;
    }

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
}
