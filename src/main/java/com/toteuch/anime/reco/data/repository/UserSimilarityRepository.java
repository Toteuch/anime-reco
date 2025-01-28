package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSimilarityRepository extends JpaRepository<UserSimilarity, Long> {
    UserSimilarity findByProfileSubAndUserUsername(String sub, String username);
}
