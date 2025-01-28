package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.UserSimilarityRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.maluser.MalUserScoreService;
import com.toteuch.anime.reco.domain.maluser.MalUserService;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UserSimilarityService {
    private static final Logger log = LoggerFactory.getLogger(UserSimilarityService.class);

    @Autowired
    private UserSimilarityRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private MalUserService userService;
    @Autowired
    private MalUserScoreService userScoreService;

    public List<MalUser> getMostSimilarUsers(String sub, Limit limit) {
        return repo.getMostSimilarUsers(sub, Sort.by(Sort.Direction.DESC, "us.score"), limit);
    }

    public UserSimilarity getUserSimilarity(Profile profile, MalUser user) {
        return repo.findByProfileSubAndUserUsername(profile.getSub(), user.getUsername());
    }

    public List<UserSimilarity> getUserSimilarities(String sub) {
        return repo.findByProfileSub(sub);
    }

    public UserSimilarity save(UserSimilarity userSimilarity) {
        return repo.save(userSimilarity);
    }

    public UserSimilarity create(String sub, String username, Double score) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("createUserSimilarity failed, Profile not found");
        MalUser user = userService.findByUsername(username);
        if (null == user) throw new AnimeRecoException("createUserSimilarity failed, User not found");
        UserSimilarity similarity = repo.findByProfileSubAndUserUsername(sub, username);
        if (null != similarity) throw new AnimeRecoException("createUserSimilarity failed, similarity already exists");
        log.debug("UserSimilarity for Profile {} and User {} : {}", sub, username, score);
        return repo.save(new UserSimilarity(profile, user, score));
    }

    public Map<Long, Double> meanCenterAnimeScoreList(List<MalUserScore> scoresToMeanCenter) {
        StringBuilder traceLog = new StringBuilder();
        for (MalUserScore s : scoresToMeanCenter) {
            traceLog.append(s.getAnime().getId()).append(": ").append(s.getScore()).append(" | ");
        }
        log.trace(traceLog.toString());
        Map<Long, Double> meanCenteredAnimeScoreMap = new HashMap<>();
        int scoresCount = scoresToMeanCenter.size();
        AtomicReference<Double> scoresSum = new AtomicReference<>(0.0);
        scoresToMeanCenter.forEach(s -> scoresSum.updateAndGet(v -> v + s.getScore()));
        double weightToApply = scoresSum.get() / scoresCount;
        scoresToMeanCenter.forEach(s ->
                meanCenteredAnimeScoreMap.put(s.getAnime().getId(), s.getScore() - weightToApply));
        traceLog = new StringBuilder();
        for (Map.Entry<Long, Double> entry : meanCenteredAnimeScoreMap.entrySet()) {
            traceLog.append(entry.getKey()).append(": ").append(entry.getValue()).append(" | ");
        }
        log.trace(traceLog.toString());
        return meanCenteredAnimeScoreMap;
    }

    public Double processSimilarity(MalUser userToProcess, Map<Long, Double> referenceUserMeanCenteredScores) {
        double similarity = -1.0;
        List<MalUserScore> userAnimeScores = userScoreService.getByUser(userToProcess);
        Map<Long, Double> meanCenteredUserMap = meanCenterAnimeScoreList(userAnimeScores);
        List<Long> commonAnimeId = getCommonAnimeIds(meanCenteredUserMap, referenceUserMeanCenteredScores);
        if (!commonAnimeId.isEmpty()) {
            log.trace("Anime Ids in common: {}", commonAnimeId);
            Double[] vector1 = new Double[commonAnimeId.size()];
            Double[] vector2 = new Double[commonAnimeId.size()];
            int index = 0;
            for (Long animeId : commonAnimeId) {
                vector1[index] = referenceUserMeanCenteredScores.get(animeId);
                vector2[index] = meanCenteredUserMap.get(animeId);
                index++;
            }
            double top = 0.0;
            double botLeft = 0.0;
            double botRight = 0.0;
            for (int i = 0; i < commonAnimeId.size(); i++) {
                top += (vector1[i] * vector2[i]);
                botLeft += Math.pow(vector1[i], 2);
                botRight += Math.pow(vector2[i], 2);
            }
            if (!isAllTheSameValues(vector2) && botLeft != 0.0 && botRight != 0.0) {
                similarity = top / (Math.sqrt(botLeft) * Math.sqrt(botRight));
            }
        }
        return similarity;
    }

    private List<Long> getCommonAnimeIds(Map<Long, Double> map1, Map<Long, Double> map2) {
        Set<Long> intersect = new HashSet<>(map1.keySet());
        intersect.retainAll(map2.keySet());
        return intersect.stream().toList();
    }

    private boolean isAllTheSameValues(Double[] vector) {
        double firstValue = vector[0];
        boolean valRet = true;
        for (Double val : vector) {
            if (val != firstValue) {
                valRet = false;
                break;
            }
        }
        return valRet;
    }
}
