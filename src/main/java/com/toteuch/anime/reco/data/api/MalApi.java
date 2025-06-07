package com.toteuch.anime.reco.data.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.toteuch.anime.reco.data.api.exception.MalApiException;
import com.toteuch.anime.reco.data.api.exception.MalApiGatewayTimeoutException;
import com.toteuch.anime.reco.data.api.exception.MalApiListNotFoundException;
import com.toteuch.anime.reco.data.api.exception.MalApiListVisibilityException;
import com.toteuch.anime.reco.data.api.response.animedetails.AnimeDetailsResponse;
import com.toteuch.anime.reco.data.api.response.animedetails.GenreResponse;
import com.toteuch.anime.reco.data.api.response.animedetails.PictureResponse;
import com.toteuch.anime.reco.data.api.response.animedetails.RelatedAnimeResponse;
import com.toteuch.anime.reco.data.api.response.animelistuser.AnimeListUserResponse;
import com.toteuch.anime.reco.data.api.response.animelistuser.Data;
import com.toteuch.anime.reco.domain.anime.AlternativeTitleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MalApi {

    private static final Logger log = LoggerFactory.getLogger(MalApi.class);
    private static final int ANIME_LIST_PAGE_SIZE = 500;
    private static final AtomicLong lastCall = new AtomicLong();
    private static WebClient webClient;

    private final Cache<Long, Mono<AnimeDetailsResponse>> animeDetailsCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofSeconds(120))
            .build();

    private final Cache<String, Mono<AnimeListUserResponse>> animeListUserCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofSeconds(120))
            .build();

    @Value("${app.data.api.mal.host}")
    private String host;

    @Value("${app.data.api.mal.auth.header.key}")
    private String authHeaderKey;

    @Value("${app.data.api.mal.auth.client.id}")
    private String authHeaderValue;

    @Value("${app.data.api.mal.request.interval}")
    private int requestInterval;

    @Value("${app.data.api.mal.anime.detail.fields}")
    private String animeDetailFields;

    private void sleepIfNeeded() {
        long now = System.currentTimeMillis();
        long last = lastCall.get();
        if (now - last < requestInterval) {
            try {
                long sleep = requestInterval - (now - last);
                log.trace("Request interval set to {} ms: MalApi sleeping for {} ms...", requestInterval, sleep);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        }
        lastCall.set(System.currentTimeMillis());
    }

    public Map<Long, UserAnimeScoreRaw> getUserAnimeListScore(String username) throws MalApiException {
        Map<Long, UserAnimeScoreRaw> retVal = new HashMap<>();
        int page = 1;

        try {
            int retryCount = 0;
            AnimeListUserResponse animelistUserResponse = null;
            while (animelistUserResponse == null) {
                log.trace("Requesting animeList of user {}, page {}, {} retry", username, page, retryCount);
                try {
                    animelistUserResponse = requestMalPublicApiGetUserAnimeList(username, page).block();
                } catch (WebClientResponseException e) {
                    if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT && retryCount < 3) {
                        retryCount++;
                    } else {
                        log.error(e.getResponseBodyAsString());
                        throw e;
                    }
                }
            }
            retVal.putAll(parseAnimeListUserResponse(animelistUserResponse, username));

            while (animelistUserResponse != null && animelistUserResponse.getPaging() != null
                    && animelistUserResponse.getPaging().getNext() != null) {
                page++;
                animelistUserResponse = null;
                retryCount = 0;
                while (animelistUserResponse == null) {
                    log.trace("Requesting animeList of user {}, page {}, {} retry", username, page, retryCount);
                    try {
                        animelistUserResponse = requestMalPublicApiGetUserAnimeList(username, page).block();
                    } catch (WebClientResponseException e) {
                        if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT && retryCount < 3) {
                            retryCount++;
                        } else {
                            throw e;
                        }
                    }
                }
                retVal.putAll(parseAnimeListUserResponse(animelistUserResponse, username));
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new MalApiListVisibilityException(HttpStatus.FORBIDDEN,
                        "User %s restricted its list visibility".formatted(username));
            } else if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new MalApiListNotFoundException(HttpStatus.NOT_FOUND,
                        "User %s not found in MAL API".formatted(username));
            } else if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
                throw new MalApiGatewayTimeoutException(e.getStatusCode(), e.getMessage());
            } else {
                throw new MalApiException(e.getStatusCode(), e.getMessage());
            }
        }

        log.trace("User {} got {} entries in its animeList", username, retVal.size());
        return retVal;
    }

    public AnimeDetailsRaw getAnimeDetails(Long animeId) throws MalApiException {
        log.trace("Requesting anime details for anime id {}", animeId);

        try {
            AnimeDetailsResponse animeDetailsResponse = requestMalPublicApiGetAnimeDetails(animeId).block();

            if (animeDetailsResponse == null) {
                throw new MalApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Anime %d details response is empty".formatted(animeId));
            }

            return parseAnimeDetailsResponse(animeDetailsResponse);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new MalApiListNotFoundException(HttpStatus.NOT_FOUND,
                        "Anime %d not found in MAL API".formatted(animeId));
            } else {
                if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
                    throw new MalApiGatewayTimeoutException(e.getStatusCode(), e.getMessage());
                } else {
                    throw new MalApiException(e.getStatusCode(), e.getMessage());
                }
            }
        }
    }

    private synchronized WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(host)
                    .defaultHeader(authHeaderKey, authHeaderValue)
                    .build();
        }
        return webClient;
    }

    private Mono<AnimeListUserResponse> requestMalPublicApiGetUserAnimeList(String username, int page) {
        String cacheKey = "{" + username + "," + page + "}";
        Mono<AnimeListUserResponse> cachedResponse = animeListUserCache.getIfPresent(cacheKey);
        if (cachedResponse != null) {
            log.trace("Using cached anime list user for username {}", username);
            return cachedResponse;
        }

        WebClient client = getWebClient();
        sleepIfNeeded();

        return animeListUserCache.get(cacheKey, requestAsKey ->
                client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/users/{username}/animelist")
                                .queryParam("offset", "{offset}")
                                .queryParam("limit", "{limit}")
                                .queryParam("fields", "{fields}")
                                .build(username, (page - 1) * ANIME_LIST_PAGE_SIZE, ANIME_LIST_PAGE_SIZE, "list_status"))
                        .retrieve()
                        .bodyToMono(AnimeListUserResponse.class)
                        .cache()
                        .doOnError(t -> animeListUserCache.invalidate(requestAsKey)));
    }

    private Mono<AnimeDetailsResponse> requestMalPublicApiGetAnimeDetails(Long animeId) {
        Mono<AnimeDetailsResponse> cachedResponse = animeDetailsCache.getIfPresent(animeId);
        if (cachedResponse != null) {
            log.trace("Using cached anime details for id {}", animeId);
            return cachedResponse;
        }

        WebClient client = getWebClient();
        sleepIfNeeded();

        return animeDetailsCache.get(animeId, requestAsKey ->
                client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/anime/{animeId}")
                                .queryParam("fields", "{fields}")
                                .build(animeId, animeDetailFields))
                        .retrieve()
                        .bodyToMono(AnimeDetailsResponse.class)
                        .cache()
                        .doOnError(t -> animeDetailsCache.invalidate(requestAsKey)));
    }

    private Map<Long, UserAnimeScoreRaw> parseAnimeListUserResponse(AnimeListUserResponse animelistUserResponse,
                                                                    String username) {
        Map<Long, UserAnimeScoreRaw> retVal = new HashMap<>();

        for (Data data : animelistUserResponse.getData()) {
            retVal.put(data.getNode().getId(), new UserAnimeScoreRaw(
                    username,
                    data.getNode().getId(),
                    data.getNode().getTitle(),
                    data.getListStatus().getScore()
            ));
        }
        return retVal;
    }

    private AnimeDetailsRaw parseAnimeDetailsResponse(AnimeDetailsResponse animeDetailsResponse) {
        AnimeDetailsRaw animeDetailsRaw = new AnimeDetailsRaw();
        animeDetailsRaw.setId(animeDetailsResponse.getId());
        animeDetailsRaw.setMediaType(animeDetailsResponse.getMediaType());
        animeDetailsRaw.setScore(animeDetailsResponse.getMean());
        animeDetailsRaw.setTitle(animeDetailsResponse.getTitle());
        animeDetailsRaw.setStatus(animeDetailsResponse.getStatus());

        if (animeDetailsResponse.getGenres() != null) {
            Map<Long, String> genres = new HashMap<>();
            for (GenreResponse genreResponse : animeDetailsResponse.getGenres()) {
                genres.put(genreResponse.getId(), genreResponse.getName());
            }
            animeDetailsRaw.setGenres(genres);
        }

        animeDetailsRaw.setNumEpisodes(animeDetailsResponse.getNumEpisodes());

        if (animeDetailsResponse.getRelatedAnimes() != null) {
            for (RelatedAnimeResponse relatedAnimeResponse : animeDetailsResponse.getRelatedAnimes()) {
                if (relatedAnimeResponse.getRelationType().equals("sequel")) {
                    animeDetailsRaw.setSequelAnimeId(relatedAnimeResponse.getNode().getId());
                } else if (relatedAnimeResponse.getRelationType().equals("prequel")) {
                    animeDetailsRaw.setPrequelAnimeId(relatedAnimeResponse.getNode().getId());
                }
            }
        }

        if (animeDetailsResponse.getMainPicture() != null) {
            animeDetailsRaw.setMainPictureUrlMedium(animeDetailsResponse.getMainPicture().getMedium());
        }

        if (animeDetailsResponse.getAlternativeTitles() != null) {
            Map<String, Object> alternativeTitles = new HashMap<>();
            alternativeTitles.put(AlternativeTitleType.EN.name(), animeDetailsResponse.getAlternativeTitles().getEn());
            alternativeTitles.put(AlternativeTitleType.JA.name(), animeDetailsResponse.getAlternativeTitles().getJa());
            alternativeTitles.put(AlternativeTitleType.SYNONYM.name(), animeDetailsResponse.getAlternativeTitles().getSynonyms());
            animeDetailsRaw.setAlternativeTitles(alternativeTitles);
        }

        animeDetailsRaw.setStartDate(animeDetailsResponse.getStartDate());
        animeDetailsRaw.setEndDate(animeDetailsResponse.getEndDate());

        if (animeDetailsResponse.getStartSeason() != null) {
            animeDetailsRaw.setStartSeasonYear(animeDetailsResponse.getStartSeason().getYear());
            animeDetailsRaw.setStartSeasonSeason(animeDetailsResponse.getStartSeason().getSeason());
        }

        animeDetailsRaw.setSource(animeDetailsResponse.getSource());
        animeDetailsRaw.setRating(animeDetailsResponse.getRating());

        if (animeDetailsResponse.getPictures() != null) {
            List<String> pictureUrlsMedium = new ArrayList<>();
            for (PictureResponse picture : animeDetailsResponse.getPictures()) {
                pictureUrlsMedium.add(picture.getMedium());
            }
            animeDetailsRaw.setPictureUrlsMedium(pictureUrlsMedium);
        }
        return animeDetailsRaw;
    }

}