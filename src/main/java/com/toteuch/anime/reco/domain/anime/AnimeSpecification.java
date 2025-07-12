package com.toteuch.anime.reco.domain.anime;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.maluser.entity.MalUserScore;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import com.toteuch.anime.reco.domain.profile.entities.Recommendation;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
import com.toteuch.anime.reco.domain.profile.entities.WatchlistAnime;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimeSpecification {

    public static Specification<Anime> notWatchedAnime(Profile profile) {
        return (root, query, builder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<MalUserScore> subRoot = subquery.from(MalUserScore.class);
            Predicate forProfile = builder.equal(subRoot.join("user").join("profile").get("id"), profile.getId());
            Predicate inWatched =
                    root.get("id").in(subquery.select(subRoot.join("anime").get("id")).where(forProfile)).not();
            return builder.and(inWatched);
        };
    }

    public static Specification<Anime> notWatchlistedAnime(Profile profile) {
        return ((root, query, builder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<WatchlistAnime> subRoot = subquery.from(WatchlistAnime.class);
            Predicate forProfile = builder.equal(subRoot.join("profile").get("id"), profile.getId());
            Predicate inWatchlist =
                    root.get("id").in(subquery.select(subRoot.join("anime").get("id")).where(forProfile)).not();
            return builder.and(inWatchlist);
        });
    }

    public static Specification<Anime> getSpecification(SearchFilter searchFilter, boolean sortByAnimeScore) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Title
            if (!StringUtils.isAllBlank(searchFilter.getTitle())) {
                Expression<Boolean> likeMainTitle = builder.like(builder.lower(root.get("title")),
                        "%" + searchFilter.getTitle() + "%".toLowerCase());
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<Anime> subRoot = subquery.from(Anime.class);
                Predicate inAltTitles = builder.like(builder.lower(subRoot.join("alternativeTitles").get("text")),
                        "%" + searchFilter.getTitle() + "%".toLowerCase());
                subquery.select(subRoot.get("id")).where(inAltTitles);
                Expression<Boolean> likeAltTitles = root.get("id").in(subquery);
                predicates.add(builder.or(likeMainTitle, likeAltTitles));
            }
            // Media Types
            if (searchFilter.getMediaTypes() != null && !searchFilter.getMediaTypes().isEmpty()) {
                predicates.add(root.get("mediaType").in(MediaType.getMalCode(searchFilter.getMediaTypes())));
            }
            // Status
            if (searchFilter.getStatusList() != null && !searchFilter.getStatusList().isEmpty()) {
                predicates.add(root.get("status").in(Status.getMalCodes(searchFilter.getStatusList())));
            }
            // Min season year
            if (searchFilter.getMinSeasonYear() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.join("season").get("year"), searchFilter.getMinSeasonYear()));
            }
            // Max season year
            if (searchFilter.getMaxSeasonYear() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.join("season").get("year"), searchFilter.getMaxSeasonYear()));
            }
            // Genres
            if (searchFilter.getGenres() != null && !searchFilter.getGenres().isEmpty()) {
                for (Genre genre : searchFilter.getGenres()) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Anime> subRoot = subquery.from(Anime.class);
                    Predicate inGenre = subRoot.join("genres").get("id").in(genre.getId());
                    subquery.select(subRoot.get("id")).where(inGenre);
                    predicates.add(root.get("id").in(subquery));
                }
            }
            // Negative genres
            if (searchFilter.getNegativeGenres() != null && !searchFilter.getNegativeGenres().isEmpty()) {
                List<Long> negativeGenreIds = new ArrayList<>();
                searchFilter.getNegativeGenres().forEach(ng -> negativeGenreIds.add(ng.getId()));
                // Subquery retrieving all anime with genresOut
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Anime> subRoot = subquery.from(Anime.class);
                Predicate inGenres = subRoot.join("genres").get("id").in(negativeGenreIds);
                subquery.select(subRoot.get("id")).where(inGenres);
                // excluding the anime retrieved by subquery
                predicates.add(root.get("id").in(subquery).not());
            }

            if (sortByAnimeScore) {
                query.orderBy(builder.desc(root.get("score")));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            } else {
                Subquery<Double> recoSubquery = query.subquery(Double.class);
                Root<Recommendation> recoRoot = recoSubquery.from(Recommendation.class);
                Predicate animeMatch = builder.equal(recoRoot.get("anime").get("id"), root.get("id"));
                Predicate profileMatch = builder.equal(recoRoot.get("profile").get("id"), searchFilter.getProfile().getId());
                recoSubquery.select(recoRoot.get("score"))
                        .where(builder.and(animeMatch, profileMatch));
                query.orderBy(builder.desc(recoSubquery));

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        };
    }
}
