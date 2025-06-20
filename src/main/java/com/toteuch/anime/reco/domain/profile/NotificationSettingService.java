package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.NotificationSettingRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.Author;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.entities.NotificationSetting;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationSettingService {
    private static final Logger log = LoggerFactory.getLogger(NotificationSettingService.class);

    @Autowired
    private NotificationSettingRepository repo;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private ProfileService profileService;

    public NotificationSetting enableNotifications(String sub, Long animeId, Author author) throws AnimeRecoException {
        Anime anime = animeService.findById(animeId).orElseGet(() -> {
                    Anime newAnime = new Anime(animeId);
                    animeService.save(newAnime);
                    return newAnime;
                }
        );
        Profile profile = profileService.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("enableNotifications failed, profile not found");
        NotificationSetting notificationSetting = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (notificationSetting == null) {
            notificationSetting = repo.save(new NotificationSetting(profile, anime, author));
            log.debug("Notifications enabled for Profile {} (Anime: {}) with AUTHOR {}", sub, animeId, author.name());
        }
        return notificationSetting;
    }

    public void disableNotifications(String sub, Long animeId) {
        NotificationSetting notificationSetting = repo.findByProfileSubAndAnimeId(sub, animeId);
        if (notificationSetting != null) {
            notificationSetting.getProfile().getNotificationSettings().remove(notificationSetting);
            notificationSetting.getAnime().getNotificationSettings().remove(notificationSetting);
            repo.delete(notificationSetting);
            log.debug("Notifications disabled for Profile {} (Anime: {})", sub, animeId);
        }
    }

    public void disableAllNotifications(String sub) {
        List<NotificationSetting> notificationSettings = repo.findByProfileSub(sub);
        if (notificationSettings != null && !notificationSettings.isEmpty()) {
            for (NotificationSetting notificationSetting : notificationSettings) {
                notificationSetting.getAnime().getNotificationSettings().remove(notificationSetting);
                notificationSetting.getProfile().getNotificationSettings().remove(notificationSetting);
            }
            repo.deleteAll(notificationSettings);
            log.debug("All notifications disabled for Profile {}", sub);
        }
    }
}
