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
        DefaultOidcUser user = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileService.findBySub(user.getSubject());
        model.addAttribute("sub", profile.getSub());
        model.addAttribute("username", profile.getUser() != null ? profile.getUser().getUsername() : "");
        return "home";
    }
}
