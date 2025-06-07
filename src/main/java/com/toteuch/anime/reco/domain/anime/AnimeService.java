package com.toteuch.anime.reco.domain.anime;

import com.toteuch.anime.reco.data.api.AnimeDetailsRaw;
import com.toteuch.anime.reco.data.api.MalApi;
import com.toteuch.anime.reco.data.api.exception.MalApiException;
import com.toteuch.anime.reco.data.api.exception.MalApiGatewayTimeoutException;
import com.toteuch.anime.reco.data.api.exception.MalApiListNotFoundException;
import com.toteuch.anime.reco.data.repository.*;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.entity.*;
import com.toteuch.anime.reco.domain.profile.FavoriteService;
import com.toteuch.anime.reco.domain.profile.NotificationService;
import com.toteuch.anime.reco.domain.profile.NotificationType;
import com.toteuch.anime.reco.domain.profile.entities.Favorite;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnimeService {
    private static final Logger log = LoggerFactory.getLogger(AnimeService.class);
    private static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_YM = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat SDF_Y = new SimpleDateFormat("yyyy");

    @Autowired
    private AnimeRepository repo;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private SeasonRepository seasonRepository;
    @Autowired
    private AlternativeTitleRepository alternativeTitleRepository;
    @Autowired
    private PictureLinkRepository pictureLinkRepository;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MalApi malApi;

    @Value("${app.domain.anime.refresh.interval}")
    private int refreshIntervalInDays;

    public Anime getById(Long id) {
        Anime anime = repo.findById(id).orElse(null);
        if (anime == null) {
            anime = new Anime(id);
            anime = repo.save(anime);
            log.trace("Anime {} created", id);
        }
        return anime;
    }

    public Page<Anime> getAnimeListToProcess(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<Anime> findById(Long id) {
        return repo.findById(id);
    }

    public void delete(Long animeId) {
        Anime anime = findById(animeId).orElse(null);
        if (anime != null) {
            repo.delete(anime);
            log.trace("Anime {} deleted", animeId);
        }
    }

    public List<Long> getNewAnimes() {
        return repo.findByDetailsUpdateIsNull(Sort.by(Sort.Direction.ASC, "id"), Limit.of(20));
    }

    public List<Long> getOldAnimes() {
        Calendar limitDate = Calendar.getInstance();
        limitDate.add(Calendar.DAY_OF_MONTH, -refreshIntervalInDays);
        return repo.findByDetailsUpdateBefore(limitDate.getTime(), Sort.by(Sort.Direction.ASC, "detailsUpdate"),
                Limit.of(20));
    }

    public void collectAnimeDetails() {
        log.debug("collectAnimeDetails starting...");
        List<Long> animeIds = getNewAnimes();
        log.trace("{} anime to process", animeIds.size());
        for (Long animeId : animeIds) {
            boolean isFound = true;
            AnimeDetailsRaw rawDetails = null;
            try {
                rawDetails = malApi.getAnimeDetails(animeId);
            } catch (MalApiListNotFoundException e) {
                log.debug(e.getMessage());
                isFound = false;
            } catch (MalApiGatewayTimeoutException e) {
                continue;
            } catch (MalApiException e) {
                log.error(e.getMessage());
                continue;
            }
            refreshAnimeDetails(animeId, rawDetails, isFound);
        }
        log.debug("collectAnimeDetails completed");
    }

    public void refreshOldAnimeDetails() {
        log.debug("refreshOldAnimeDetails starting...");
        List<Long> animeIds = getOldAnimes();
        log.trace("{} anime to process", animeIds.size());
        for (Long animeId : animeIds) {
            boolean isFound = true;
            AnimeDetailsRaw rawDetails = null;
            try {
                rawDetails = malApi.getAnimeDetails(animeId);
            } catch (MalApiListNotFoundException e) {
                log.debug(e.getMessage());
                isFound = false;
            } catch (MalApiGatewayTimeoutException e) {
                continue;
            } catch (MalApiException e) {
                log.error(e.getMessage());
                continue;
            }
            refreshAnimeDetails(animeId, rawDetails, isFound);
        }
        log.debug("refreshOldAnimeDetails completed");
    }

    private void refreshAnimeDetails(Long animeId, AnimeDetailsRaw rawDetails, boolean isFound) {
        log.debug("Starting refreshing anime details for anime {} ...", animeId);
        if (!isFound) {
            delete(animeId);
            return;
        }
        Anime anime = findById(animeId).orElse(null);
        Long previousSequelId = anime.getSequelAnimeId();
        String previousStatus = anime.getStatus();
        anime.setTitle(rawDetails.getTitle());
        anime.setMediaType(rawDetails.getMediaType());
        anime.setNumEpisodes(rawDetails.getNumEpisodes());
        anime.setPrequelAnimeId(rawDetails.getPrequelAnimeId());
        anime.setSequelAnimeId(rawDetails.getSequelAnimeId());
        anime.setScore(rawDetails.getScore());
        anime.setDetailsUpdate(new Date());
        anime.setMainPictureMediumUrl(rawDetails.getMainPictureUrlMedium());
        anime.setStatus(rawDetails.getStatus());
        anime.setStartDate(parseRawDate(rawDetails.getStartDate()));
        anime.setEndDate(parseRawDate(rawDetails.getEndDate()));
        anime.setSource(rawDetails.getSource());
        anime.setRating(rawDetails.getRating());
        // GENRES
        List<Genre> genres = genreRepository.findByAnime(anime);
        if (genres == null) genres = new ArrayList<>();
        Map<Long, Genre> existingGenreMap = new HashMap<>();
        for (Genre genre : genres) {
            existingGenreMap.put(genre.getId(), genre);
        }
        genres.clear();
        if (rawDetails.getGenres() != null) {
            for (Map.Entry<Long, String> entry : rawDetails.getGenres().entrySet()) {
                Genre genre = existingGenreMap.get(entry.getKey());
                if (genre == null) {
                    genre = new Genre(entry.getKey(), entry.getValue());
                }
                genres.add(genreRepository.save(genre));
            }
        }
        anime.setGenres(genres);
        // ALTERNATIVE TITLES
        List<AlternativeTitle> alternativeTitles = alternativeTitleRepository.findByAnime(anime);
        if (alternativeTitles == null) alternativeTitles = new ArrayList<>();
        Map<AlternativeTitleType, Object> existingATMap = new HashMap<>();
        for (AlternativeTitle at : alternativeTitles) {
            if (at.getType() == AlternativeTitleType.SYNONYM) {
                List<AlternativeTitle> synonyms = (List<AlternativeTitle>) existingATMap.get(AlternativeTitleType.SYNONYM);
                if (synonyms == null) synonyms = new ArrayList<>();
                synonyms.add(at);
                existingATMap.put(AlternativeTitleType.SYNONYM, synonyms);
            } else {
                existingATMap.put(at.getType(), at);
            }
        }
        alternativeTitles.clear();
        for (Map.Entry<String, Object> entry : rawDetails.getAlternativeTitles().entrySet()) {
            if (entry.getKey().equals(AlternativeTitleType.EN.name())) {
                String rawAT = (String) entry.getValue();
                AlternativeTitle at = (AlternativeTitle) existingATMap.get(AlternativeTitleType.EN);
                if (at == null) at = new AlternativeTitle(anime, AlternativeTitleType.EN);
                at.setText(rawAT);
                alternativeTitles.add(alternativeTitleRepository.save(at));
            } else if (entry.getKey().equals(AlternativeTitleType.JA.name())) {
                String rawAT = (String) entry.getValue();
                AlternativeTitle at = (AlternativeTitle) existingATMap.get(AlternativeTitleType.JA);
                if (at == null) at = new AlternativeTitle(anime, AlternativeTitleType.JA);
                at.setText(rawAT);
                alternativeTitles.add(alternativeTitleRepository.save(at));
            } else {
                String[] rawSynonyms = (String[]) entry.getValue();
                for (String rawSynonym : rawSynonyms) {
                    AlternativeTitle at = new AlternativeTitle(anime, AlternativeTitleType.SYNONYM, rawSynonym);
                    alternativeTitles.add(alternativeTitleRepository.save(at));
                }
            }
        }
        anime.setAlternativeTitles(alternativeTitles);
        // SEASON
        Season season = null;
        if (rawDetails.getStartSeasonYear() != null && rawDetails.getStartSeasonSeason() != null) {
            season = seasonRepository.findByYearAndSeason(rawDetails.getStartSeasonYear(),
                    rawDetails.getStartSeasonSeason());
            if (season == null) {
                season = new Season(rawDetails.getStartSeasonYear(), rawDetails.getStartSeasonSeason());
                season = seasonRepository.save(season);
            }
        }
        anime.setSeason(season);
        // PICTURE LINK
        List<PictureLink> pictureLinks = pictureLinkRepository.findByAnime(anime);
        List<String> existingMediums = new ArrayList<>();
        pictureLinks.forEach(pl -> existingMediums.add(pl.getMedium()));
        for (String medium : rawDetails.getPictureUrlsMedium()) {
            if (!existingMediums.contains(medium)) {
                PictureLink pl = new PictureLink(anime, medium);
                pictureLinks.add(pictureLinkRepository.save(pl));
            }
        }
        anime.setPictureLinks(pictureLinks);
        // FAVORITE
        List<Favorite> favorites = anime.getFavorites();
        if (anime.getSequelAnimeId() != null && previousSequelId == null && !anime.getFavorites().isEmpty()) {
            for (Favorite favorite : anime.getFavorites()) {
                Profile profile = favorite.getProfile();
                try {
                    favoriteService.create(profile.getSub(), anime.getSequelAnimeId(), Author.SYSTEM);
                } catch (AnimeRecoException e) {
                    log.error("Failed to create favorite for Profile {} (Anime: {}) WITH author SYSTEM: {}",
                            profile.getSub(), anime.getSequelAnimeId(), e.getMessage());
                }
            }
        }
        // NOTIFICATION
        if (previousStatus != null && !previousStatus.equals(anime.getStatus()) && anime.getFavorites() != null) {
            for (Favorite favorite : anime.getFavorites()) {
                try {
                    notificationService.create(favorite.getProfile().getSub(), animeId,
                            NotificationType.FAVORITE_ANIME_STATUS_CHANGED);
                } catch (AnimeRecoException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private Date parseRawDate(String sDate) {
        if (!StringUtils.hasText(sDate)) {
            return null;
        }
        try {
            if (sDate.length() > 7) {
                return SDF_FULL.parse(sDate);
            } else if (sDate.length() > 4) {
                return SDF_YM.parse(sDate);
            } else {
                return SDF_Y.parse(sDate);
            }
        } catch (ParseException e) {
            log.error("Couldn't parse date {}", sDate);
            return null;
        }
    }

    public void save(Anime newAnime) {
        repo.save(newAnime);
    }
}
