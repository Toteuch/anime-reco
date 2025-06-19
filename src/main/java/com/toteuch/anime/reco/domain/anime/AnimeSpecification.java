package com.toteuch.anime.reco.domain.anime;

import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.entity.Genre;
import com.toteuch.anime.reco.domain.profile.entities.SearchFilter;
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

    public static Specification<Anime> getSpecification(SearchFilter searchFilter) {
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
                predicates.add(root.get("mediaType").in(searchFilter.getMediaTypes()));
            }
            // Status
            if (searchFilter.getStatusList() != null && !searchFilter.getStatusList().isEmpty()) {
                predicates.add(root.get("status").in(searchFilter.getStatusList()));
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
            query.orderBy(builder.desc(root.get("score")));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
