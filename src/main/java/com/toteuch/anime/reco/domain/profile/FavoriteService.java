package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.FavoriteRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.entities.Favorite;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {
    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    @Autowired
    private FavoriteRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AnimeService animeService;


    public Favorite create(String sub, Long animeId, Author author) throws AnimeRecoException {
        Anime anime = animeService.findById(animeId).orElseGet(() -> {
                    Anime newAnime = new Anime(animeId);
                    animeService.save(newAnime);
                    return newAnime;
                }
        );
        Profile profile = profileService.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("createFavorite failed, profile not found");
        Favorite favorite = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (favorite == null) {
            log.debug("Favorite created for Profile {} (Anime: {}) with AUTHOR {}", sub, animeId, author.name());
            favorite = repo.save(new Favorite(profile, anime, author));
        }
        return favorite;
    }

    public void delete(String sub, Long animeId) {
        Favorite favorite = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (favorite != null) {
            log.debug("Favorite deleted for Profile {} (Anime: {})", sub, animeId);
            repo.delete(favorite);
        }
    }

    public void deleteAll(String sub) {
        List<Favorite> favorites = repo.findByProfileSub(sub);
        if (favorites != null && !favorites.isEmpty()) {
            log.debug("All favorites deleted for Profile {}", sub);
            repo.deleteAll(favorites);
        }
    }

    public void favoriteRelatedAnime(Profile profile, Anime anime, Map<Long, MalUserScore> existingScoresMap,
                                     boolean isSequel) {
        Long relatedAnimeId = isSequel ? anime.getSequelAnimeId() : anime.getPrequelAnimeId();
        while (relatedAnimeId != null) {
            Long finalRelatedAnime = relatedAnimeId;
            Anime relatedAnime = animeService.findById(relatedAnimeId).orElseGet(() -> {
                Anime newAnime = new Anime(finalRelatedAnime);
                animeService.save(newAnime);
                return newAnime;
            });
            if (!existingScoresMap.containsKey(relatedAnimeId) && repo.findByProfileSubAndAnimeId(profile.getSub(),
                    relatedAnimeId) == null) {
                try {
                    create(profile.getSub(), relatedAnimeId, Author.SYSTEM);
                } catch (AnimeRecoException e) {
                    log.error("Failed to create favorite for Profile {} (Anime: {}) WITH author SYSTEM: {}",
                            profile.getSub(), relatedAnimeId, e.getMessage());
                }
            }
            relatedAnimeId = isSequel ? relatedAnime.getSequelAnimeId() : relatedAnime.getPrequelAnimeId();
        }
    }

    public void favoriteRelatedAnime(Profile profile, Anime anime, Map<Long, MalUserScore> existingScoresMap) {
        favoriteRelatedAnime(profile, anime, existingScoresMap, true);
        favoriteRelatedAnime(profile, anime, existingScoresMap, false);
    }
}
