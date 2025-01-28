package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSimilarityRepository extends JpaRepository<UserSimilarity, Long> {
    UserSimilarity findByProfileSubAndUserUsername(String sub, String username);

    List<UserSimilarity> findByProfileSub(String sub);

    @Query("SELECT mu FROM MalUser mu " +
            "JOIN UserSimilarity us ON mu = us.user AND us.profile.sub = :sub ")
    List<MalUser> getMostSimilarUsers(String sub, Sort sort, Limit limit);
}
