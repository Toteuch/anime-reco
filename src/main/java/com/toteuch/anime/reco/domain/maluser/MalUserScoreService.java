package com.toteuch.anime.reco.domain.maluser;

import com.toteuch.anime.reco.data.api.MalApi;
import com.toteuch.anime.reco.data.api.UserAnimeScoreRaw;
import com.toteuch.anime.reco.data.api.exception.MalApiException;
import com.toteuch.anime.reco.data.api.exception.MalApiGatewayTimeoutException;
import com.toteuch.anime.reco.data.api.exception.MalApiListNotFoundException;
import com.toteuch.anime.reco.data.api.exception.MalApiListVisibilityException;
import com.toteuch.anime.reco.data.repository.MalUserScoreRepository;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MalUserScoreService {
    private static final Logger log = LoggerFactory.getLogger(MalUserScoreService.class);

    @Autowired
    private MalUserScoreRepository repo;
    @Autowired
    private MalUserService userService;
    @Autowired
    private MalApi malApi;
    @Autowired
    private AnimeService animeService;

    public void collectUserScores() {
        log.debug("collectUserScores starting...");
        List<String> usernames = userService.getNewUsers();
        log.trace("{} users to process", usernames.size());
        for (String username : usernames) {
            List<UserAnimeScoreRaw> rawScores = null;
            boolean isListVisible = true;
            boolean isFound = true;
            try {
                rawScores = malApi.getUserAnimeListScore(username);
            } catch (MalApiListVisibilityException e) {
                log.debug(e.getMessage());
                isListVisible = false;
            } catch (MalApiListNotFoundException e) {
                log.debug(e.getMessage());
                isFound = false;
            } catch (MalApiGatewayTimeoutException e) {
                continue;
            } catch (MalApiException e) {
                log.error(e.getMessage());
                continue;
            }
            refreshUserScores(username, rawScores, isFound, isListVisible);
        }
        log.debug("collectUserScores completed");
    }

    public void refreshUpdatedUserScores() {
        log.debug("refreshUpdatedUserScores starting...");
        List<String> usernames = userService.getUpdatedUsers();
        log.trace("{} users to process", usernames.size());
        for (String username : usernames) {
            List<UserAnimeScoreRaw> rawScores = null;
            boolean isListVisible = true;
            boolean isFound = true;
            try {
                rawScores = malApi.getUserAnimeListScore(username);
            } catch (MalApiListVisibilityException e) {
                log.debug(e.getMessage());
                isListVisible = false;
            } catch (MalApiListNotFoundException e) {
                log.debug(e.getMessage());
                isFound = false;
            } catch (MalApiGatewayTimeoutException e) {
                continue;
            } catch (MalApiException e) {
                log.error(e.getMessage());
                continue;
            }
            refreshUserScores(username, rawScores, isFound, isListVisible);
        }
        log.debug("refreshUpdatedUserScores completed");
    }

    public void refreshOldUserScores() {
        log.debug("refreshOldUserScores starting...");
        List<String> usernames = userService.getOldUsers();
        log.trace("{} users to process", usernames.size());
        for (String username : usernames) {
            List<UserAnimeScoreRaw> rawScores = null;
            boolean isListVisible = true;
            boolean isFound = true;
            try {
                rawScores = malApi.getUserAnimeListScore(username);
            } catch (MalApiListVisibilityException e) {
                log.debug(e.getMessage());
                isListVisible = false;
            } catch (MalApiListNotFoundException e) {
                log.debug(e.getMessage());
                isFound = false;
            } catch (MalApiGatewayTimeoutException e) {
                continue;
            } catch (MalApiException e) {
                log.error(e.getMessage());
                continue;
            }
            refreshUserScores(username, rawScores, isFound, isListVisible);
        }
        log.debug("refreshOldUserScores completed");
    }


    private void refreshUserScores(String username, List<UserAnimeScoreRaw> rawScores, boolean isFound,
                                   boolean isListVisible) {
        log.debug("Starting refreshing user scores for user {} ...", username);
        if (!isFound) {
            userService.delete(username);
            log.trace("User scores refresh for user {} is complete", username);
            return;
        }
        MalUser user = userService.findByUsername(username);
        if (!isListVisible) {
            user.setAnimeListSize(0);
            user.setAnimeRatedCount(0);
            user.setLastUpdate(new Date());
            user.setListVisible(false);
            user.setScores(null);
            userService.save(user);
            log.trace("User scores refresh for user {} is complete", username);
            return;
        }
        user.setListVisible(true);
        user.setAnimeListSize(rawScores.size());
        user.setLastUpdate(new Date());

        List<MalUserScore> scores = repo.findByUser(user);
        if (scores == null) scores = new ArrayList<>();
        Map<Long, MalUserScore> existingScoreMap = new HashMap<>();
        for (MalUserScore score : scores) {
            existingScoreMap.put(score.getAnime().getId(), score);
        }
        scores.clear();
        for (UserAnimeScoreRaw rawScore : rawScores) {
            MalUserScore score = existingScoreMap.get(rawScore.getAnimeId());
            if (score == null) {
                Anime anime = animeService.getById(rawScore.getAnimeId());
                score = new MalUserScore(user, anime);
            }
            score.setScore(rawScore.getUserScore());
            if (score.getScore() > 0) {
                scores.add(repo.save(score));
            }
        }
        user.setScores(scores);
        user.setAnimeRatedCount(scores.size());
        userService.save(user);
        log.trace("User scores refresh for user {} is complete", username);
    }
}
