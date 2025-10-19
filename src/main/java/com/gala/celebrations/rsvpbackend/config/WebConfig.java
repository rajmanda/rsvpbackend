package com.gala.celebrations.rsvpbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Injects the comma-separated list of URLs from your application.yaml
    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private String[] allowedOrigins;


//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
////                .allowedOrigins("http://localhost:4200")
//                .allowedOrigins("http://localhost:4200", "http://rajmanda-dev.com")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }

//    @Override
//    public void addCorsMappings(@NonNull CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")  // Allow any domain
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(false);  // Credentials are not allowed with wildcard origins
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedOrigins("http://localhost:4200", "https://rajmanda-dev.com", "https://shravanikalyanam.com")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

