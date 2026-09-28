package com.lucky.collabtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CollaborativeTaskPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollaborativeTaskPlatformApplication.class, args);
    }
}
