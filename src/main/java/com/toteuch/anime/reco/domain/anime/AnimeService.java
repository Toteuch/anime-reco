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
import com.toteuch.anime.reco.domain.profile.NotificationService;
import com.toteuch.anime.reco.domain.profile.NotificationSettingService;
import com.toteuch.anime.reco.domain.profile.NotificationType;
import com.toteuch.anime.reco.domain.profile.RecommendationService;
import com.toteuch.anime.reco.domain.profile.entities.NotificationSetting;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.toteuch.anime.reco.domain.anime.SeasonLabel.*;
import static com.toteuch.anime.reco.domain.anime.Status.FINISHED;

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
    private NotificationService notificationService;
    @Autowired
    private NotificationSettingService notificationSettingService;
    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private MalApi malApi;

    @Cacheable(value = "dailyStats", key = "'animeCount'")
    public Long countAnime() {
        return repo.count();
    }

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
        return repo.findByDetailsUpdateBefore(Sort.by(Sort.Direction.ASC, "detailsUpdate"),
                Limit.of(20));
    }

    public Page<Anime> getWatched(Profile profile, int pageSize, int page) {
        return repo.findWatchedByProfile(profile, PageRequest.of(page, pageSize));
    }

    public Page<Anime> getWatchlist(Profile profile, boolean full, int pageSize, int page) {
        if (full) {
            return repo.findWatchlistByProfile(profile, PageRequest.of(page, pageSize));
        } else {
            return repo.findWatchlistByProfileAndCompleted(profile, PageRequest.of(page, pageSize));
        }
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
        Date previousStartDate = anime.getStartDate();
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
        anime.setSourceCreatedAt(parseRawDate(rawDetails.getCreatedAt()));
        anime.setSourceUpdatedAt(parseRawDate(rawDetails.getUpdatedAt()));
        anime.setBackground(rawDetails.getBackground());
        anime.setSynopsis(rawDetails.getSynopsis());
        anime.setNsfw(rawDetails.getNsfw());
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
        // NOTIFICATION SETTINGS
        List<NotificationSetting> notificationSettings = anime.getNotificationSettings();
        if (anime.getSequelAnimeId() != null && previousSequelId == null && !anime.getNotificationSettings().isEmpty()) {
            for (NotificationSetting notificationSetting : anime.getNotificationSettings()) {
                Profile profile = notificationSetting.getProfile();
                try {
                    notificationSettingService.enableNotifications(profile.getSub(), anime.getSequelAnimeId(), Author.SYSTEM);
                } catch (AnimeRecoException e) {
                    log.error("Failed to enable notifications for Profile {} (Anime: {}) WITH author SYSTEM: {}",
                            profile.getSub(), anime.getSequelAnimeId(), e.getMessage());
                }
            }
        }
        save(anime);
        // NOTIFICATION - New related anime
        if (previousSequelId == null && previousSequelId != anime.getSequelAnimeId() && anime.getNotificationSettings() != null) {
            for (NotificationSetting notificationSetting : anime.getNotificationSettings()) {
                try {
                    notificationService.create(notificationSetting.getProfile().getSub(), animeId,
                            NotificationType.NEW_RELATED_ANIME);
                } catch (AnimeRecoException e) {
                    log.error(e.getMessage());
                }
            }
        } else if (previousStatus != null && !previousStatus.equals(anime.getStatus()) && anime.getNotificationSettings() != null) {
            // NOTIFICATION - Status changed
            for (NotificationSetting notificationSetting : anime.getNotificationSettings()) {
                try {
                    notificationService.create(notificationSetting.getProfile().getSub(), animeId,
                            NotificationType.STATUS_CHANGED);
                } catch (AnimeRecoException e) {
                    log.error(e.getMessage());
                }
            }
        } else if (!StringUtils.equals(previousStatus, FINISHED.getMalCode())
                && previousStartDate != anime.getStartDate() && anime.getNotificationSettings() != null) {
            // NOTIFICATION - Start date changed
            for (NotificationSetting notificationSetting : anime.getNotificationSettings()) {
                try {
                    notificationService.create(notificationSetting.getProfile().getSub(), animeId,
                            NotificationType.START_DATE_CHANGED);
                } catch (AnimeRecoException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private Date parseRawDate(String sDate) {
        if (StringUtils.isAllBlank(sDate)) {
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

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    public Map<Long, String> getGenresMap() {
        Map<Long, String> genresMap = new HashMap<>();
        List<Genre> genres = genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        genres.forEach(g -> genresMap.put(g.getId(), g.getName()));
        return genresMap;
    }

    public Page<Anime> search(SearchFilter searchFilter, int pageSize, int page) throws AnimeRecoException {
        Specification<Anime> specs = AnimeSpecification.getSpecification(searchFilter, true);
        return repo.findAll(specs, PageRequest.of(page, pageSize));
    }

    public Page<Anime> getCurrentSeasonAnimePage(int pageSize, int page) throws AnimeRecoException {
        Season currentSeason = seasonRepository.findByYearAndSeason(getCurrentSeasonYear(), getCurrentSeason().getMalCode());
        if (currentSeason != null) {
            return repo.findBySeason(currentSeason, PageRequest.of(page, pageSize));
        }
        return null;
    }

    public SeasonLabel getCurrentSeason() throws AnimeRecoException {
        return getSeasonFromDate(new Date());
    }

    public Integer getCurrentSeasonYear() {
        Calendar cal = Calendar.getInstance();
        int seasonYear = cal.get(Calendar.YEAR);
        Calendar previousWinterStart = getStartSeason(WINTER);
        previousWinterStart.add(Calendar.YEAR, -1);
        Calendar springStart = getStartSeason(SPRING);
        if (cal.after(previousWinterStart) && cal.before(springStart)) {
            seasonYear--;
        }
        return seasonYear;
    }

    private SeasonLabel getSeasonFromDate(Date date) throws AnimeRecoException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar previousWinterStart = getStartSeason(WINTER);
        previousWinterStart.add(Calendar.YEAR, -1);
        Calendar springStart = getStartSeason(SPRING);
        Calendar summerStart = getStartSeason(SUMMER);
        Calendar fallStart = getStartSeason(FALL);
        Calendar winterStart = getStartSeason(WINTER);
        if ((cal.after(previousWinterStart) && cal.before(springStart))
                || cal.after(winterStart)) {
            return WINTER;
        } else if (cal.after(springStart) && cal.before(summerStart)) {
            return SPRING;
        } else if (cal.after(summerStart) && cal.before(fallStart)) {
            return SUMMER;
        } else if (cal.after(fallStart) && cal.before(winterStart)) {
            return FALL;
        } else {
            throw new AnimeRecoException("Couldn't find the current season");
        }
    }

    private Calendar getStartSeason(SeasonLabel season) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.MONTH, season.getFromMonth());
        start.set(Calendar.DAY_OF_MONTH, season.getFromDay());
        start.set(Calendar.HOUR, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start;
    }

    public Page<Anime> getRecommendations(SearchFilter searchFilter, int pageSize) throws AnimeRecoException {
        if (searchFilter == null) throw new AnimeRecoException("Filter for recommendations null.");
        Specification<Anime> specs = null;
        Profile profile = searchFilter.getProfile();
        if (profile == null || recommendationService.getRecommendationsCount(profile) < pageSize) {
            specs = AnimeSpecification.getSpecification(searchFilter, true);
        } else {
            specs = AnimeSpecification.getSpecification(searchFilter, false);
        }

        if (profile != null) {
            specs = specs.and(AnimeSpecification.notWatchedAnime(profile));
            specs = specs.and(AnimeSpecification.notExcluded(profile));
            if (profile.getExcludeWatchListFromRecommendation()) {
                specs = specs.and(AnimeSpecification.notWatchlistedAnime(profile));
            }
        }
        return repo.findAll(specs, PageRequest.of(0, pageSize));
    }
}
