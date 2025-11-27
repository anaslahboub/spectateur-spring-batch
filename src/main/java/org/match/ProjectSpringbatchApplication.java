package org.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjectSpringbatchApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProjectSpringbatchApplication.class, args);
    }

}
