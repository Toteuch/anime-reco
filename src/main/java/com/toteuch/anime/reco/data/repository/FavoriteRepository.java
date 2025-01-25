package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByProfileSubAndAnimeId(String sub, Long animeId);

    List<Favorite> findByProfileSub(String sub);
}
