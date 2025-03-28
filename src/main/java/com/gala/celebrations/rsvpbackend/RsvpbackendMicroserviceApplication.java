package com.gala.celebrations.rsvpbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class RsvpbackendMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsvpbackendMicroserviceApplication.class, args);
	}

}
