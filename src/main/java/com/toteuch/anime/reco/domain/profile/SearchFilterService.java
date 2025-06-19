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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFilterService {
    private static final Logger log = LoggerFactory.getLogger(SearchFilterService.class);

    @Autowired
    private SearchFilterRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AnimeService animeService;

    public SearchFilter createFilter(String sub, String name, List<MediaType> mediaTypes, List<Status> statusList,
                                     Integer minSeasonYear, Integer maxSeasonYear, List<Long> genreIds,
                                     List<Long> negativeGenreIds) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("Profile not found");
        SearchFilter searchFilter = repo.findByProfileAndName(profile, name);
        if (searchFilter != null) throw new AnimeRecoException("Name already used");
        searchFilter = new SearchFilter();
        searchFilter.setProfile(profile);
        searchFilter.setName(name);
        searchFilter.setMediaTypes(mediaTypes);
        searchFilter.setStatusList(statusList);
        List<Genre> genres = new ArrayList<>();
        if (genreIds != null) genreIds.forEach(g -> genres.add(animeService.getGenreById(g)));
        searchFilter.setGenres(genres);
        List<Genre> negativeGenres = new ArrayList<>();
        if (negativeGenreIds != null) negativeGenreIds.forEach((g -> negativeGenres.add(animeService.getGenreById(g))));
        searchFilter.setNegativeGenres(negativeGenres);
        searchFilter.setMinSeasonYear(minSeasonYear == null || minSeasonYear == 0 ? null : minSeasonYear);
        searchFilter.setMaxSeasonYear(maxSeasonYear == null || maxSeasonYear == 0 ? null : maxSeasonYear);
        searchFilter = repo.save(searchFilter);
        log.debug("SearchFilter {} created for user {}", name, profile.getUser().getUsername());
        return searchFilter;
    }

    public SearchFilter validate(SearchFilterPojo searchFilterPojo) throws AnimeRecoException {
        if (searchFilterPojo.getId() != null) {
            throw new AnimeRecoException("Not implemented yet");
        }
        SearchFilter searchFilter = new SearchFilter();
        if (searchFilterPojo.getName() != null) {
            throw new AnimeRecoException("Not implemented yet");
        }
        if (StringUtils.isAllBlank(searchFilterPojo.getTitle())) searchFilterPojo.setTitle(null);
        searchFilter.setTitle(searchFilterPojo.getTitle());
        if (searchFilterPojo.getMinSeasonYear() != null
                && searchFilterPojo.getMinSeasonYear() == 0) searchFilterPojo.setMaxSeasonYear(null);
        if (searchFilterPojo.getMaxSeasonYear() != null
                && searchFilterPojo.getMaxSeasonYear() == 0) searchFilterPojo.setMaxSeasonYear(null);
        if (searchFilterPojo.getMinSeasonYear() != null && searchFilterPojo.getMaxSeasonYear() != null
                && searchFilterPojo.getMinSeasonYear() > searchFilterPojo.getMaxSeasonYear()) {
            throw new AnimeRecoException("Inconsistent Min/Max season year");
        }
        searchFilter.setMinSeasonYear(searchFilterPojo.getMinSeasonYear());
        searchFilter.setMaxSeasonYear(searchFilterPojo.getMaxSeasonYear());
        if (searchFilterPojo.getMediaTypes() == null) searchFilterPojo.setMediaTypes(new ArrayList<>());
        searchFilter.setMediaTypes(MediaType.parseMediaTypes(searchFilterPojo.getMediaTypes()));
        if (searchFilterPojo.getStatusList() == null) searchFilterPojo.setStatusList(new ArrayList<>());
        searchFilter.setStatusList(Status.parseStatusList(searchFilterPojo.getStatusList()));
        if (searchFilterPojo.getGenres() == null) searchFilterPojo.setGenres(new ArrayList<>());
        searchFilter.setGenres(parseGenreList(searchFilterPojo.getGenres()));
        if (searchFilterPojo.getNegativeGenres() == null) searchFilterPojo.setNegativeGenres(new ArrayList<>());
        searchFilter.setNegativeGenres(parseGenreList(searchFilterPojo.getNegativeGenres()));
        return searchFilter;
    }

    private List<Genre> parseGenreList(List<Long> genreIds) throws AnimeRecoException {
        List<Genre> genres = new ArrayList<>();
        for (Long genreId : genreIds) {
            Genre genre = animeService.getGenreById(genreId);
            if (genre == null) {
                log.error("Genre %s not found".formatted(genreId));
                throw new AnimeRecoException("Genre %s not found".formatted(genreId));
            }
            genres.add(genre);
        }
        return genres;
    }
}
