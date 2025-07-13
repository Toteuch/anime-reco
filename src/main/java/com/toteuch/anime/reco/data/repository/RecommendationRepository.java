package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Recommendation findByProfileAndAnime(Profile profile, Anime anime);

    @Query("SELECT COUNT(reco) FROM Recommendation reco WHERE reco.profile = :profile")
    Integer countByProfile(Profile profile);

    List<Recommendation> findByProfileAndExcludeIsTrue(Profile profile);
}
