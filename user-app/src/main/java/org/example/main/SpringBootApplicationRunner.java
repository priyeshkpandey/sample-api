package org.example.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.example")
@EntityScan("org.example.data.model")
public class SpringBootApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationRunner.class, args);
    }
}
