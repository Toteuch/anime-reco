package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.FavoriteRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.entities.Favorite;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Anime anime = animeService.findById(animeId).orElse(null);
        if (anime == null) throw new AnimeRecoException("createFavorite failed, anime not found");
        Profile profile = profileService.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("createFavorite failed, profile not found");
        Favorite favorite = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (favorite == null) {
            log.debug("Favorite create for Profile {} (Anime: {})", sub, animeId);
            favorite = repo.save(new Favorite(profile, anime, author));
        }
        return favorite;
    }

    public void delete(String sub, Long animeId) {
        Favorite favorite = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (favorite != null) {
            repo.delete(favorite);
        }
    }
}
