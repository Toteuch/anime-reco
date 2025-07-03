package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.SearchFilterRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.MediaType;
import com.toteuch.anime.reco.domain.anime.Status;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.pojo.SearchFilterPojo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFilterService {
    private static final Logger log = LoggerFactory.getLogger(SearchFilterService.class);

    @Autowired
    private SearchFilterRepository repo;
    @Autowired
    private AnimeService animeService;

    public SearchFilter saveSearchFilter(Profile profile, SearchFilterPojo pojo) throws AnimeRecoException {
        if (profile == null) throw new AnimeRecoException("Profile is not found");
        SearchFilter searchFilter = validate(pojo);
        if (searchFilter.getProfile() != null && searchFilter.getProfile() != profile)
            throw new AnimeRecoException("Internal error.");
        searchFilter.setProfile(profile);
        if (searchFilter.getName() == null) throw new AnimeRecoException("Filter name is required.");
        SearchFilter existingSF = isSearchFilterNameAlreadyUsed(profile, searchFilter.getName());
        if (existingSF != null && !existingSF.getId().equals(searchFilter.getId())) {
            throw new AnimeRecoException("Filter name must be unique");
        }
        if (searchFilter.getFilterIndex() == null) {
            searchFilter.setFilterIndex(getNextOrderAvailable(profile));
        }
        return repo.save(searchFilter);
    }

    private int getNextOrderAvailable(Profile profile) {
        SearchFilter maxOrder = repo.findByProfileOrderByFilterIndexDesc(profile, Limit.of(1));
        if (maxOrder == null) return 0;
        return maxOrder.getFilterIndex() + 1;
    }

    public SearchFilter isSearchFilterNameAlreadyUsed(Profile profile, String name) {
        if (name == null) return null;
        return repo.findByProfileAndName(profile, name.trim());
    }

    public SearchFilter validate(SearchFilterPojo searchFilterPojo) throws AnimeRecoException {
        SearchFilter searchFilter = null;
        if (searchFilterPojo.getId() != null && searchFilterPojo.getId() != 0L) {
            searchFilter = repo.getReferenceById(searchFilterPojo.getId());
        } else {
            searchFilter = new SearchFilter();
        }
        if (searchFilterPojo.getName() != null && !StringUtils.isAllBlank(searchFilterPojo.getName()))
            searchFilter.setName(searchFilterPojo.getName().trim());
        if (searchFilterPojo.getTitle() != null) searchFilter.setTitle(searchFilterPojo.getTitle().trim());
        if (searchFilterPojo.getMediaTypes() != null && !searchFilterPojo.getMediaTypes().isEmpty()) {
            List<MediaType> mediaTypes = new ArrayList<>();
            for (String mediaTypeCode : searchFilterPojo.getMediaTypes()) {
                MediaType mediaType = MediaType.getByMalCode(mediaTypeCode);
                if (mediaType == null)
                    throw new AnimeRecoException("Media type %1$s unknown.".formatted(mediaTypeCode));
                mediaTypes.add(mediaType);
            }
            searchFilter.setMediaTypes(mediaTypes);
        }
        if (searchFilterPojo.getStatusList() != null && !searchFilterPojo.getStatusList().isEmpty()) {
            List<Status> statusList = new ArrayList<>();
            for (String statusCode : searchFilterPojo.getStatusList()) {
                Status status = Status.getByMalCode(statusCode);
                if (status == null) throw new AnimeRecoException("Status %1$s unknown.".formatted(statusCode));
                statusList.add(status);
            }
            searchFilter.setStatusList(statusList);
        }
        if (searchFilterPojo.getMinSeasonYear() == null || searchFilterPojo.getMinSeasonYear() == 0)
            searchFilterPojo.setMinSeasonYear(null);
        if (searchFilterPojo.getMaxSeasonYear() == null || searchFilterPojo.getMaxSeasonYear() == 0)
            searchFilterPojo.setMaxSeasonYear(null);
        if (searchFilterPojo.getMinSeasonYear() != null && searchFilterPojo.getMaxSeasonYear() != null
                && searchFilterPojo.getMinSeasonYear() > searchFilterPojo.getMaxSeasonYear()) {
            throw new AnimeRecoException("Inconsistent Min/Max season year.");
        }
        searchFilter.setMinSeasonYear(searchFilterPojo.getMinSeasonYear());
        searchFilter.setMaxSeasonYear(searchFilterPojo.getMaxSeasonYear());
        if (searchFilterPojo.getGenres() != null && !searchFilterPojo.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            for (Long genreId : searchFilterPojo.getGenres()) {
                Genre genre = animeService.getGenreById(genreId);
                if (genre == null) throw new AnimeRecoException("Genre %1$s unknown.".formatted(genreId));
                genres.add(genre);
            }
            searchFilter.setGenres(genres);
        }
        if (searchFilterPojo.getNegativeGenres() != null && !searchFilterPojo.getNegativeGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            for (Long genreId : searchFilterPojo.getNegativeGenres()) {
                Genre genre = animeService.getGenreById(genreId);
                if (genre == null) throw new AnimeRecoException("Genre %1$s unknown.".formatted(genreId));
                genres.add(genre);
            }
            searchFilter.setNegativeGenres(genres);
        }
        return searchFilter;
    }
}
