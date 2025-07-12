package com.toteuch.anime.reco.domain.profile;

import com.toteuch.anime.reco.data.repository.ProfileRepository;
import com.toteuch.anime.reco.domain.AnimeRecoException;
import com.toteuch.anime.reco.domain.maluser.MalUserService;
import com.toteuch.anime.reco.domain.maluser.entity.MalUser;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileRepository repo;
    @Autowired
    private MalUserService userService;


    public List<Profile> getAll() {
        return repo.findAll();
    }

    public Profile save(Profile profile) {
        return repo.save(profile);
    }

    public Profile create(String sub, String email, String avatarUrl) throws AnimeRecoException {
        if (!StringUtils.hasText(sub) || !StringUtils.hasText(email) || !StringUtils.hasText(avatarUrl)) {
            throw new AnimeRecoException("createProfile failed, required field to create a Profile is missing");
        }
        Profile profileSub = repo.findBySub(sub);
        if (profileSub != null) throw new AnimeRecoException("createProfile failed, this sub already exists");
        Profile profileEmail = repo.findByEmail(email);
        if (profileEmail != null) throw new AnimeRecoException("createProfile failed, this email already exists");
        log.debug("Profile {} created", sub);
        return repo.save(new Profile(sub, email, avatarUrl));
    }

    public Profile linkMalUser(String sub, String username) throws AnimeRecoException {
        Profile profile = repo.findBySub(sub);
        if (profile == null) throw new AnimeRecoException("Profile doesn't exist");
        if (profile.getUser() != null && username.equals(profile.getUser().getUsername())) return profile;
        Profile profileUser = repo.findByUserUsername(username);
        if (profileUser != null)
            throw new AnimeRecoException("The user is already linked to a profile");
        if (profile.getUser() != null) {
            MalUser previousUser = profile.getUser();
            previousUser.setProfile(null);
            profile.setUser(null);
            userService.save(previousUser);
        }
        MalUser user = userService.findByUsername(username);
        if (user == null) {
            user = new MalUser(username);
            log.debug("User {} created", username);
        }
        user.setProfile(profile);
        userService.save(user);
        profile.setUser(user);
        log.debug("Profile {} linked to user {}", sub, username);
        return repo.save(profile);
    }

    public Profile setExcludeWatchlistFromRecommendations(Profile profile, boolean exclude) {
        profile.setExcludeWatchListFromRecommendation(exclude);
        return repo.save(profile);
    }

    public Profile findBySub(String sub) {
        return repo.findBySub(sub);
    }
}
