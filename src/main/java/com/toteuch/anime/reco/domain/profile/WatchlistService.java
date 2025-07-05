package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.WatchlistAnimeRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.WatchlistAnime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.toteuch.anime.reco.domain.anime.AnimeService.WATCHLIST_PAGE_SIZE;

@Service
public class WatchlistService {
    private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);

    @Autowired
    private WatchlistAnimeRepository repo;
    @Autowired
    private AnimeService animeService;

    public WatchlistAnime save(Profile profile, Long animeId) throws AnimeRecoException {
        if (profile == null || animeId == null)
            throw new AnimeRecoException("Internal error.");
        Anime anime = animeService.getById(animeId);
        if (anime == null) throw new AnimeRecoException("Anime not found.");
        if (repo.findByProfileAndAnime(profile, anime) != null)
            throw new AnimeRecoException("Anime already added to the watchlist.");
        if (profile.getUser() != null && !canBeAdded(profile.getUser(), anime))
            throw new AnimeRecoException("Anime already watched");
        WatchlistAnime watchlistAnime = repo.save(new WatchlistAnime(profile, anime));
        log.debug("Anime {} added to the watchlist of Profile {}",
                anime.getId(), profile.getSub());
        return watchlistAnime;
    }

    public void delete(Profile profile, Long id) throws AnimeRecoException {
        if (profile == null || id == null)
            throw new AnimeRecoException("Internal error.");
        WatchlistAnime watchlistAnime = repo.findByProfileAndAnimeId(profile, id);
        if (watchlistAnime == null || watchlistAnime.getProfile() != profile)
            throw new AnimeRecoException("Internal error.");
        Anime anime = watchlistAnime.getAnime();
        repo.delete(watchlistAnime);
        log.debug("Anime {} removed from the watchlist of Profile {}",
                anime.getId(), profile.getSub());
    }

    public Page<Anime> getWatchlist(Profile profile, int index) {
        return repo.getWatchlist(profile, PageRequest.of(index, WATCHLIST_PAGE_SIZE));
    }

    public boolean canBeAdded(MalUser user, Anime anime) {
        if (user.getScores() != null) {
            for (MalUserScore mus : user.getScores()) {
                if (mus.getAnime() == anime) return false;
            }
        }
        return true;
    }

    public void removeFromWatchlist(MalUser user, Anime anime) {
        if (user.getProfile() != null) {
            List<WatchlistAnime> watchlist = repo.findByProfile(user.getProfile());
            watchlist.forEach(wa -> {
                if (wa.getAnime() == anime) {
                    repo.delete(wa);
                    log.debug("Anime {} removed from the watchlist of Profile {}",
                            anime.getId(), user.getProfile().getSub());
                }
            });
        }
    }

    public WatchlistAnime getByProfileAndAnime(Profile profile, Anime anime) {
        return repo.findByProfileAndAnime(profile, anime);
    }
}
