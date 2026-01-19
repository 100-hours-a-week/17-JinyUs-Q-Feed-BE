package com.ktb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QFeedApplication {
    static void main(String[] args) {
        SpringApplication.run(QFeedApplication.class, args);
    }
}
