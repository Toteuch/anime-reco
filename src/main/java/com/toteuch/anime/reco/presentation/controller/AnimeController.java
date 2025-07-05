package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.*;
import com.toteuch.anime.reco.domain.anime.entity.AlternativeTitle;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.anime.pojo.AnimeDetailsPojo;
import com.toteuch.anime.reco.domain.anime.pojo.AnimePojo;
import com.toteuch.anime.reco.domain.profile.NotificationSettingService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.SearchFilterService;
import com.toteuch.anime.reco.domain.profile.WatchlistService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.entities.WatchlistAnime;
import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;
import com.toteuch.anime.reco.presentation.controller.response.AnimeDetailsResponse;
import com.toteuch.anime.reco.presentation.controller.response.AnimeListResultResponse;
import jakarta.websocket.server.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class AnimeController {

    private static final Logger log = LoggerFactory.getLogger(AnimeController.class);

    @Autowired
    private SearchFilterService searchFilterService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private NotificationSettingService notificationSettingService;
    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/anime/search")
    public AnimeListResultResponse search(@RequestBody SearchFilterPojo searchFilterPojo) {
        try {
            SearchFilter searchFilter = searchFilterService.validate(searchFilterPojo);
            Page<Anime> animeList = animeService.search(searchFilter, searchFilterPojo.getPageNumber());
            return new AnimeListResultResponse(getAnimeListPojo(animeList), animeList.getNumber(),
                    animeList.getTotalPages());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new AnimeListResultResponse(ex.getMessage());
        }
    }

    @GetMapping("/anime/watched/{index}")
    public AnimeListResultResponse getWatched(@PathVariable int index) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            Page<Anime> animePage = animeService.getWatched(profile, index);
            return new AnimeListResultResponse(getAnimeListPojo(animePage), animePage.getNumber(),
                    animePage.getTotalPages());
        } else {
            return new AnimeListResultResponse("You must be logged in.");
        }
    }

    @PostMapping("/anime/{id}/notifications")
    public AnimeDetailsResponse setNotifications(@PathVariable Long id, @PathParam("enable") boolean enable) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            try {
                if (enable) {
                    notificationSettingService.enableNotifications(oidcUser.getSubject(), id, Author.USER);
                } else {
                    notificationSettingService.disableNotifications(oidcUser.getSubject(), id);
                }
            } catch (AnimeRecoException ex) {
                log.error(ex.getMessage(), ex);
                return new AnimeDetailsResponse("Internal error");
            }
        } else {
            return new AnimeDetailsResponse("You must be logged in");
        }
        Anime anime = animeService.getById(id);
        Profile profile = profileService.findBySub(oidcUser.getSubject());
        return new AnimeDetailsResponse(getAnimeDetailsPojo(anime, profile));
    }

    private List<AnimePojo> getAnimeListPojo(Page<Anime> animeList) {
        List<AnimePojo> animeListPojo = new ArrayList<>();
        if (animeList != null && !animeList.isEmpty()) {
            for (Anime anime : animeList.stream().toList()) {
                AnimePojo pojo = new AnimePojo();
                pojo.setId(anime.getId());
                pojo.setMainMediumUrl(anime.getMainPictureMediumUrl());
                pojo.setTitle(getMainTitle(anime));
                pojo.setAltTitles(getAltTitles(anime));
                animeListPojo.add(pojo);
            }
        }
        return animeListPojo;
    }

    @GetMapping("/anime/{id}")
    public AnimeDetailsResponse getAnimeDetails(@PathVariable Long id) {
        Anime anime = animeService.getById(id);
        if (anime == null) {
            return new AnimeDetailsResponse("Internal error");
        }
        Profile profile = null;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            profile = profileService.findBySub(oidcUser.getSubject());
        }
        return new AnimeDetailsResponse(getAnimeDetailsPojo(anime, profile));
    }

    @PostMapping("/anime/{id}/watchlist")
    public AnimeDetailsResponse addToWatchlist(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            try {
                WatchlistAnime watchlistAnime = watchlistService.save(profile, id);
                return new AnimeDetailsResponse(getAnimeDetailsPojo(watchlistAnime.getAnime(), profile));
            } catch (AnimeRecoException ex) {
                return new AnimeDetailsResponse(ex.getMessage());
            }
        } else {
            return new AnimeDetailsResponse("You must be logged in.");
        }
    }

    @DeleteMapping("/anime/{id}/watchlist")
    public AnimeDetailsResponse removeFromWatchlist(@PathVariable Long id) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            try {
                watchlistService.delete(profile, id);
                Anime anime = animeService.getById(id);
                return new AnimeDetailsResponse(getAnimeDetailsPojo(anime, profile));
            } catch (AnimeRecoException ex) {
                return new AnimeDetailsResponse(ex.getMessage());
            }
        } else {
            return new AnimeDetailsResponse("You must be logged in.");
        }
    }

    private AnimeDetailsPojo getAnimeDetailsPojo(Anime anime, Profile profile) {
        AnimeDetailsPojo pojo = new AnimeDetailsPojo();
        pojo.setId(anime.getId());
        pojo.setMainTitle(getMainTitle(anime));
        pojo.setMediaTypeLabel(MediaType.getByMalCode(anime.getMediaType()) != null ?
                MediaType.getByMalCode(anime.getMediaType()).getLabel() : null);
        pojo.setNumEpisodes(anime.getNumEpisodes());
        pojo.setPrequelAnimeId(anime.getPrequelAnimeId());
        pojo.setSequelAnimeId(anime.getSequelAnimeId());
        pojo.setAnimeScore(anime.getScore());
        pojo.setLastUpdate(anime.getDetailsUpdate());
        pojo.setMainPictureMediumUrl(anime.getMainPictureMediumUrl());
        Status status = Status.getByMalCode(anime.getStatus());
        pojo.setStatusLabel(status != null ? status.getLabel() : anime.getStatus());
        pojo.setStartDate(anime.getStartDate());
        pojo.setEndDate(anime.getEndDate());
        if (anime.getSeason() != null)
            pojo.setSeasonLabel(SeasonLabel.getLabel(anime.getSeason()) + " " + anime.getSeason().getYear());
        pojo.setGenreLabels(Genre.getGenresLabels(anime.getGenres()));
        pojo.setAlternativeTitles(getAltTitles(anime));
        List<String> picLinks = new ArrayList<>();
        anime.getPictureLinks().forEach(pl -> picLinks.add(pl.getMedium()));
        pojo.setPictureLinks(picLinks);
        pojo.setInWatchlist(profile != null && watchlistService.getByProfileAndAnime(profile, anime) != null);
        AtomicBoolean notifications = new AtomicBoolean(false);
        if (profile != null) {
            pojo.setAddableToWatchlist(profile.getUser() == null || watchlistService.canBeAdded(profile.getUser(), anime));
            profile.getNotificationSettings().forEach(ns -> {
                if (ns.getAnime() == anime) {
                    notifications.set(true);
                }
            });
        }
        pojo.setNotificationsEnabled(notifications.get());
        return pojo;
    }

    private String getMainTitle(Anime anime) {
        for (AlternativeTitle altTitle : anime.getAlternativeTitles()) {
            if (altTitle.getType() == AlternativeTitleType.EN && !StringUtils.isAllBlank(altTitle.getText())) {
                return altTitle.getText();
            }
        }
        return anime.getTitle();
    }

    private List<String> getAltTitles(Anime anime) {
        List<String> altTitles = new ArrayList<>();
        for (AlternativeTitle altTitle : anime.getAlternativeTitles()) {
            if (altTitle.getType() == AlternativeTitleType.EN && !StringUtils.isAllBlank(altTitle.getText())) {
                altTitles.add(anime.getTitle());
            } else if (!StringUtils.isAllBlank(altTitle.getText())) {
                altTitles.add(altTitle.getText());
            }
        }
        return altTitles;
    }
}
