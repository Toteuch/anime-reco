package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.anime.entity.AlternativeTitle;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternativeTitleRepository extends JpaRepository<AlternativeTitle, Long> {
    List<AlternativeTitle> findByAnime(Anime anime);
}
