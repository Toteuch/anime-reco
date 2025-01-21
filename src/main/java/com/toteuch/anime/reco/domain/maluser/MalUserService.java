package com.toteuch.anime.reco.domain.maluser;

import com.toteuch.anime.reco.data.repository.MalUserRepository;
import com.toteuch.anime.reco.data.scrap.ScrapLastSeenUsers;
import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MalUserService {
    private static final Logger log = LoggerFactory.getLogger(MalUserService.class);

    @Autowired
    private MalUserRepository repo;
    @Autowired
    private ScrapLastSeenUsers scrapLastSeenUsers;
    @Autowired
    private AnimeService animeService;

    @Value("${app.domain.maluser.refresh.interval}")
    private int refreshIntervalInDays;

    public void scrapLastSeenUsers() {
        log.trace("Scrap MAL's last seen usernames starting...");
        List<String> usernameList = scrapLastSeenUsers.getUsernamesFromMAL();
        log.trace("Found {} usernames from MAL's last seen user page", usernameList.size());
        for (String username : usernameList) {
            refreshMalUserLastSeen(username);
        }
        log.trace("Scrap MAL's last seen usernames completed");
    }

    public List<String> getNewUsers() {
        return repo.findByLastUpdateIsNull(Sort.by(Sort.Direction.ASC, "lastSeen"), Limit.of(30));
    }

    public List<String> getUpdatedUsers() {
        return repo.findByLastSeenGreaterThanLastUpdate(Sort.by(Sort.Direction.ASC, "lastSeen"), Limit.of(30));
    }

    public List<String> getOldUsers() {
        Calendar limitDate = Calendar.getInstance();
        limitDate.add(Calendar.DAY_OF_MONTH, -refreshIntervalInDays);
        return repo.findByLastUpdateBefore(limitDate.getTime(), Sort.by(Sort.Direction.ASC, "lastUpdate"),
                Limit.of(30));
    }

    public void delete(String username) {
        MalUser user = findByUsername(username);
        if (user != null) {
            repo.delete(user);
            log.trace("User {} deleted", username);
        }
    }

    public MalUser save(MalUser malUser) {
        return repo.save(malUser);
    }

    public MalUser findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void refreshMalUserLastSeen(String username) {
        MalUser user = repo.findByUsername(username);
        if (user == null) {
            user = new MalUser(username);
        }
        user.setLastSeen(new Date());
        log.trace("User {} {}", username, user.getId() != null ? "updated" : "created");
        repo.save(user);
    }


}
