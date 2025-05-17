package ru.internship.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@EnableSwagger2
//@Import(BeanValidatorPluginsConfiguration.class)
public class InternshipPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternshipPlatformApplication.class, args);
    }
}
