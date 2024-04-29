package org.example.xchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(XchangeApplication.class, args);
    }

}
