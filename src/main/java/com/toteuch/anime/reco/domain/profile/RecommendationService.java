package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.RecommendationRepository;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    private RecommendationRepository repo;

    public void save(Recommendation reco) {
        repo.save(reco);
    }

    public Integer getRecommendationsCount(Profile profile) {
        return repo.countByProfile(profile);
    }

    public Double processRecommendationRate(Map<MalUser, Double> relevantUserMap, Anime animeToProcess, Map<MalUser,
            Map<Long, Double>> usersScores) {
        double recommendationRate = 0.0;
        double topSum = 0.0;
        double botSum = 0.0;
        int countUsers = 0;

        for (Map.Entry<MalUser, Double> entry : relevantUserMap.entrySet()) {
            Double score = usersScores.get(entry.getKey()).get(animeToProcess.getId());
            if (score != null) {
                Double userSimilarity = entry.getValue();
                topSum += userSimilarity * score;
                botSum += userSimilarity;
                countUsers++;
            }
        }
        if (botSum > 0.0 && countUsers >= 10) {
            recommendationRate = topSum / botSum;
        }
        return recommendationRate;
    }

    public Recommendation getRecommendation(Profile profile, Anime anime) {
        return repo.findByProfileAndAnime(profile, anime);
    }

    public Recommendation excludeRecommendation(Profile profile, Anime anime, boolean exclude) {
        Recommendation recommendation = repo.findByProfileAndAnime(profile, anime);
        recommendation.setExclude(exclude);
        return repo.save(recommendation);
    }

    public List<Recommendation> getExcludedRecommendations(Profile profile) {
        return repo.findByProfileAndExcludeIsTrue(profile);
    }
}
