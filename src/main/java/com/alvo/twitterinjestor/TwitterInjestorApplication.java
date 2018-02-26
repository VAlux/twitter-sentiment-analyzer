package com.alvo.twitterinjestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.alvo")
public class TwitterInjestorApplication {

  public static void main(String[] args) {
    SpringApplication.run(TwitterInjestorApplication.class, args);
  }
}
