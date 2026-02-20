package com.nikitamorozov.docregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocRegistryApplication.class, args);
    }
}

