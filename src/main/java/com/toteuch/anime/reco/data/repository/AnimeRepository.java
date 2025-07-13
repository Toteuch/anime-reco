package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.entity.Season;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long>, JpaSpecificationExecutor<Anime> {
    @Query("SELECT ani.id FROM Anime ani WHERE ani.detailsUpdate IS NULL")
    List<Long> findByDetailsUpdateIsNull(Sort sort, Limit limit);

    @Query("SELECT ani.id FROM Anime ani WHERE ani.detailsUpdate < :detailsUpdate")
    List<Long> findByDetailsUpdateBefore(Date detailsUpdate, Sort sort, Limit limit);

    @Query("SELECT ani FROM MalUserScore mus " +
            "INNER JOIN Anime ani ON ani = mus.anime " +
            "INNER JOIN MalUser mu ON mus.user = mu " +
            "INNER JOIN Profile p ON p.user = mu " +
            "WHERE p = :profile " +
            "ORDER BY mus.updatedAt DESC")
    Page<Anime> findWatchedByProfile(Profile profile, Pageable pageable);

    @Query("SELECT ani FROM WatchlistAnime wa " +
            "INNER JOIN Anime ani ON ani = wa.anime " +
            "WHERE wa.profile = :profile " +
            "ORDER BY wa.createdAt")
    Page<Anime> findWatchlistByProfile(Profile profile, Pageable pageable);

    @Query("SELECT ani FROM WatchlistAnime wa " +
            "INNER JOIN Anime ani ON ani = wa.anime " +
            "WHERE wa.profile = :profile " +
            "AND ani.status = 'finished_airing' " +
            "ORDER BY wa.createdAt")
    Page<Anime> findWatchlistByProfileAndCompleted(Profile profile, Pageable pageable);

    Page<Anime> findBySeason(Season season, Pageable pageable);
}
