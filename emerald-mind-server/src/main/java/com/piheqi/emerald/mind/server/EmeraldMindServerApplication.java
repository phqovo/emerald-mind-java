package com.piheqi.emerald.mind.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = {"com.piheqi"})
public class EmeraldMindServerApplication implements CommandLineRunner {
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EmeraldMindServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("======================> Service running on port:" + environment.getProperty("server.port","8080"));
    }
}
