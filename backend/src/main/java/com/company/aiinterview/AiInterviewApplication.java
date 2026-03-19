package com.company.aiinterview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@Slf4j
public class AiInterviewApplication {
    public static void main(String[] args) {
        normalizeDatabaseUrl();
        SpringApplication.run(AiInterviewApplication.class, args);
        log.info("AI Interview backend started at http://localhost:8080");
    }

    private static void normalizeDatabaseUrl() {
        String rawUrl = System.getenv("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank() || rawUrl.startsWith("jdbc:")) {
            return;
        }
        if (!rawUrl.startsWith("postgresql://")) {
            return;
        }

        URI uri = URI.create(rawUrl);
        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isBlank()) {
            String[] userInfoParts = userInfo.split(":", 2);
            if (userInfoParts.length > 0 && !userInfoParts[0].isBlank()) {
                System.setProperty("spring.datasource.username", decode(userInfoParts[0]));
            }
            if (userInfoParts.length == 2 && !userInfoParts[1].isBlank()) {
                System.setProperty("spring.datasource.password", decode(userInfoParts[1]));
            }
        }

        StringBuilder jdbc = new StringBuilder();
        jdbc.append("jdbc:postgresql://").append(uri.getHost());
        if (uri.getPort() > 0) {
            jdbc.append(":").append(uri.getPort());
        }
        jdbc.append(uri.getRawPath());
        if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
            jdbc.append("?").append(uri.getRawQuery());
        }

        System.setProperty("spring.datasource.url", jdbc.toString());
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
