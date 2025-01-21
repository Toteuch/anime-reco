package com.toteuch.anime.reco.data.scrap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScrapLastSeenUsers {

    private static final Logger log = LoggerFactory.getLogger(ScrapLastSeenUsers.class);
    private static final String USER_LIST_URL = "https://myanimelist.net/users.php";
    private static final String DIV_ONLINE_USER_CLASS = "normal_header";
    private static final String DIV_ONLINE_USER_TEXT = "Recently Online Users";
    private static final String PROFILE_HREF_CONTAINS = "/profile/";

    @Value("${app.data.scrap.user-agent}")
    private String userAgent;
    @Value("${app.data.scrap.request-interval}")
    private long requestInterval;

    private long lastRequestTime = 0;

    public List<String> getUsernamesFromMAL() {
        List<String> parsedUsernames = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < requestInterval) {
            try {
                long sleep = requestInterval - (currentTime - lastRequestTime);
                log.trace("Scrap client sleeping for {} ms", sleep);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                log.error("Thread sleep interrupted", e);
                Thread.currentThread().interrupt();
                return parsedUsernames;
            }
        }
        try {
            log.trace("Attempt to scrap usernames from MAL last seen user page...");
            Connection.Response res = Jsoup.connect(USER_LIST_URL)
                    .userAgent(userAgent)
                    .referrer("http://www.google.com")
                    .method(Connection.Method.GET)
                    .execute();
            lastRequestTime = System.currentTimeMillis();
            if (res.statusCode() == 200) {
                Document doc = res.parse();
                Element divOnlineUser = doc.body().getElementsByClass(DIV_ONLINE_USER_CLASS).stream()
                        .filter(element -> DIV_ONLINE_USER_TEXT.equals(element.text()))
                        .findFirst()
                        .orElse(null);

                if (divOnlineUser != null && divOnlineUser.parent() != null) {
                    parsedUsernames = divOnlineUser.parent().getElementsByAttributeValueContaining("href", PROFILE_HREF_CONTAINS).stream()
                            .filter(profileElement -> profileElement.children().isEmpty())
                            .map(Element::text)
                            .collect(Collectors.toList());
                }
            } else {
                log.error("GET {} : {}-{}", USER_LIST_URL, res.statusCode(), res.statusMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return parsedUsernames;
    }
}