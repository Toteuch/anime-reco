package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import org.springframework.data.domain.Limit;
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
}
