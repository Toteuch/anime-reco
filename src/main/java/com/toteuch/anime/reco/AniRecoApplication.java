package com.toteuch.anime.reco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AniRecoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AniRecoApplication.class, args);
    }
}
