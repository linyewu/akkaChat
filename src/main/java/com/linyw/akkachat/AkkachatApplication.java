package com.linyw.akkachat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.linyw.akkachat.akka","com.linyw.akkachat.controller"})
@SpringBootApplication
public class AkkachatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkkachatApplication.class, args);
    }

}
