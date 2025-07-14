package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.NotificationRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.anime.entity.Anime;
import com.toteuch.anime.reco.domain.profile.entities.Notification;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private static final int NOTIF_DISPLAYED_COUNT = 8;

    @Autowired
    private NotificationRepository repo;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AnimeService animeService;

    public Notification create(String sub, Long animeId, NotificationType type) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("createNotification failed, Profile doesn't exist");
        Anime anime = animeService.findById(animeId).orElse(null);
        if (null == anime) throw new AnimeRecoException("createNotification failed, Anime doesn't exist");
        log.debug("Notification of type {} created for Profile {} and Anime {}", type.name(), sub, animeId);
        return repo.save(new Notification(profile, type, anime));
    }

    public Notification read(String sub, Long id) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("readNotification failed, Profile doesn't exist");
        Notification notification = repo.findByProfileAndId(profile, id);
        if (null == notification) throw new AnimeRecoException("readNotification failed, Notification doesn't exist");
        if (notification.getReadAt() == null) {
            notification.setReadAt(new Date());
            log.debug("Notification {} read for Profile {}", id, sub);
            return repo.save(notification);
        } else {
            return notification;
        }
    }

    public List<Notification> readAll(String sub) throws AnimeRecoException {
        Profile profile = profileService.findBySub(sub);
        if (null == profile) throw new AnimeRecoException("readAllNotification failed, Profile doesn't exist");
        List<Notification> notifications = repo.findByProfileSubAndReadAtNull(sub, Sort.by(Sort.Direction.ASC, "id"));
        Date readDate = new Date();
        for (Notification notification : notifications) {
            notification.setReadAt(readDate);
        }
        log.debug("All notifications read for Profile {}", sub);
        return repo.saveAll(notifications);
    }

    public List<Notification> getUnreadNotifications(String sub) {
        return repo.findByProfileSubAndReadAtNull(sub, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Sorted by creation date descending, not read first
     *
     * @return
     */
    public Page<Notification> getAllNotifications(String sub, int pageNumber) {
        return repo.findByProfileSub(sub, PageRequest.of(pageNumber, NOTIF_DISPLAYED_COUNT));
    }
}
