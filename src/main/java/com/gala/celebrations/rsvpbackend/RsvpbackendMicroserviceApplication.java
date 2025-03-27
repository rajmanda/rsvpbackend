package com.gala.celebrations.rsvpbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.gala.celebrations.rsvpbackend.config.CustomCorsFilter;

@SpringBootApplication
public class RsvpbackendMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsvpbackendMicroserviceApplication.class, args);
	}

	// @Bean
    // public FilterRegistrationBean<CustomCorsFilter> customCorsFilter() {
    //     FilterRegistrationBean<CustomCorsFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new CustomCorsFilter());
    //     registrationBean.addUrlPatterns("/*"); // Apply to all URLs
    //     System.out.println("********************* Debug: Custom CORS filter registered.");
    //     return registrationBean;
    // }

}
