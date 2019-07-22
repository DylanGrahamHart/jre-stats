package com.jrestats.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropConfig {

    @Value("${spring.profiles.active}")
    public String springActiveProfile;

    @Value("${jrestats.pagesOfVideosToGet}")
    public Integer pagesOfVideosToGet;

    public boolean isLocal() {
        return "local".equals(springActiveProfile);
    }

}
