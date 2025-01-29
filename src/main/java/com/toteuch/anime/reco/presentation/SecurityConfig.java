package com.toteuch.anime.reco.presentation;

import com.toteuch.anime.reco.domain.profile.ProfileService;
import com.toteuch.anime.reco.domain.profile.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ProfileService profileService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/*").authenticated();
                    auth.anyRequest().permitAll();
                })
                .oauth2Login(oauth2login -> {
                    oauth2login
                            .userInfoEndpoint(userInfo -> userInfo
                                    .oidcUserService(this.oidcUserService()))
                            .defaultSuccessUrl("/", true);
                })
                .logout(logout -> logout.logoutSuccessUrl("/"));


        return http.build();
    }

    @Transactional
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);

            // Check if that user exist in db
            Profile profile = profileService.findBySub(oidcUser.getSubject());
            if (profile == null) {
                profile = new Profile(oidcUser.getSubject(), oidcUser.getEmail(), oidcUser.getPicture());
                profile = profileService.save(profile);
            }

            return oidcUser;
        };
    }
}
