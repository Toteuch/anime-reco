package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MalUserScoreRepository extends JpaRepository<MalUserScore, Long> {
    List<MalUserScore> findByUser(MalUser user);

    @Modifying
    @Query("DELETE FROM MalUserScore WHERE user.id = :userId")
    void deleteByUserId(Long userId);
}
