package com.toteuch.anime.reco.presentation.controller;

import com.toteuch.anime.reco.domain.profile.ProfileService;
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
        return "home";
    }

    @GetMapping("profile")
    public String showProfile(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.PROFILE.getCode());
        return "profile";
    }

    @GetMapping("notifications")
    public String showNotification(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser oidcUser) {
            model.addAttribute("isAuthenticated", "true");
        } else {
            model.addAttribute("isAuthenticated", "false");
        }
        model.addAttribute("currentPage", TemplateName.NOTIFICATIONS.getCode());
        return "notifications";
    }

    @GetMapping("search")
    public String showSearch(Model model) {
        model.addAttribute("isAuthenticated", "true");
        model.addAttribute("currentPage", TemplateName.SEARCH.getCode());
        return "search";
    }
}
