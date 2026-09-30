package com.wormmus.student_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "https://student-frontend.moazalshareef.workers.dev",
                        "https://student-api.wormmus.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}