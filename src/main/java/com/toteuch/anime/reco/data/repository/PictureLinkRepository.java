package com.toteuch.anime.reco.data.repository;

import com.sun.jdi.LongValue;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.anime.entity.PictureLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureLinkRepository extends JpaRepository<PictureLink, LongValue> {
    List<PictureLink> findByAnime(Anime anime);
}
