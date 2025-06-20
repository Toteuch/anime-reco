package com.toteuch.anime.reco.data.repository;

import com.toteuch.anime.reco.domain.profile.entities.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    NotificationSetting findByProfileSubAndAnimeId(String sub, Long animeId);

    List<NotificationSetting> findByProfileSub(String sub);
}
