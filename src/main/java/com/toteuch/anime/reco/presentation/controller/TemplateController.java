package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.anime.AnimeService;
import com.toteuch.anime.reco.domain.job.JobName;
import com.toteuch.anime.reco.domain.job.JobStatus;
import com.toteuch.anime.reco.domain.job.JobTaskService;
import com.toteuch.anime.reco.domain.job.entities.JobTask;
import com.toteuch.anime.reco.domain.maluser.MalUserService;
import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.SearchFilterService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private SearchFilterService searchFilterService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private MalUserService malUserService;
    @Autowired
    private JobTaskService jobTaskService;

    @GetMapping({"/", "/index.html"})
    public String showHome(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            model.addAttribute("sub", profile.getSub());
            model.addAttribute("username", profile.getUser() != null ? profile.getUser().getUsername() : "");
            model.addAttribute("isAuthenticated", "true");
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.HOME.getCode());
        addStatistics(model);
        return "home";
    }

    @GetMapping("profile")
    public String showProfile(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            if (profile.getUser() != null) {
                model.addAttribute("isUserLinked", "true");
            } else {
                model.addAttribute("isUserLinked", "false");
            }

        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.PROFILE.getCode());
        addStatistics(model);
        return "profile";
    }

    @GetMapping("search")
    public String showSearch(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.SEARCH.getCode());
        addStatistics(model);
        return "search";
    }

    @GetMapping("recommendations")
    public String showRecommendations(Model model) {
        model.addAttribute("lastJobCompleted", "true");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
            JobTask jobTask = jobTaskService.getLastOccurrence(oidcUser.getSubject(),
                    JobName.PROCESS_ANIME_RECOMMENDATION, null);
            if (jobTask == null || jobTask.getStatus() != JobStatus.COMPLETED) {
                model.addAttribute("lastJobCompleted", "false");
            }
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.RECOMMENDATIONS.getCode());
        addStatistics(model);
        return "recommendations";
    }

    @GetMapping("watchlist")
    public String showWatchlist(Model model) {
        model.addAttribute("isUserLinked", "false");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            if (profile.getUser() != null) {
                model.addAttribute("isUserLinked", "true");
            }
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.WATCHLIST.getCode());
        addStatistics(model);
        return "watchlist";
    }

    private void addStatistics(Model model) {
        model.addAttribute("animeCount", animeService.countAnime());
        model.addAttribute("userCount", malUserService.countUser());
    }
}
