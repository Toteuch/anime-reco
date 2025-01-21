package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    Season findByYearAndSeason(Integer year, String season);
}
