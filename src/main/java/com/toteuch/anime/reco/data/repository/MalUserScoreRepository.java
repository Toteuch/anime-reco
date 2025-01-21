package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MalUserScoreRepository extends JpaRepository<MalUserScore, Long> {
    List<MalUserScore> findByUser(MalUser user);
}
