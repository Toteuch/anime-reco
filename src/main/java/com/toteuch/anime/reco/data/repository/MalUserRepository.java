package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MalUserRepository extends JpaRepository<MalUser, Long> {
    MalUser findByUsername(String username);

    @Query("SELECT mu.username FROM MalUser mu WHERE mu.lastUpdate IS NULL")
    List<String> findByLastUpdateIsNull(Sort sort, Limit limit);

    @Query("SELECT mu.username FROM MalUser mu WHERE mu.lastSeen > mu.lastUpdate AND mu.isListVisible = true")
    List<String> findByLastSeenGreaterThanLastUpdate(Sort sort, Limit limit);

    @Query("SELECT mu.username FROM MalUser mu WHERE mu.lastUpdate < :limitLastUpdate and mu.isListVisible = true")
    List<String> findByLastUpdateBefore(Date limitLastUpdate, Sort sort, Limit limit);

    @Query("SELECT mu FROM MalUser mu " +
            "LEFT JOIN UserSimilarity us ON mu = us.user AND us.profile.id = :profileId " +
            "LEFT JOIN Profile p ON mu = p.user AND p.id = :profileId " +
            "WHERE (us.score IS NULL OR us.lastUpdate > :startDate) " +
            "AND mu.animeRatedCount > 0 " +
            "AND p.id IS NULL")
    Page<MalUser> getUserListToProcess(Long profileId, Date startDate, Pageable pageable);


}
