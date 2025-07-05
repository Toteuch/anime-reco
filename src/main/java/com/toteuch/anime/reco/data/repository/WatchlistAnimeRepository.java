package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.WatchlistAnime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistAnimeRepository extends JpaRepository<WatchlistAnime, Long> {
    WatchlistAnime findByProfileAndAnime(Profile profile, Anime anime);

    @Query("SELECT ani FROM WatchlistAnime wa " +
            "INNER JOIN Anime ani ON wa.anime = ani " +
            "WHERE wa.profile = :profile " +
            "ORDER BY wa.createdAt")
    Page<Anime> getWatchlist(Profile profile, Pageable pageable);

    List<WatchlistAnime> findByProfile(Profile profile);

    @Query("SELECT wa FROM WatchlistAnime wa " +
            "INNER JOIN Anime ani ON wa.anime = ani " +
            "WHERE wa.profile =:profile " +
            "AND ani.id = :animeId")
    WatchlistAnime findByProfileAndAnimeId(Profile profile, Long animeId);
}
