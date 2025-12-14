package com.example.tubesrpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TubesrplApplication {

    public static void main(String[] args) {
        SpringApplication.run(TubesrplApplication.class, args);
    }
}
