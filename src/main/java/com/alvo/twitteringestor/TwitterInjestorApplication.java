package com.alvo.twitteringestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan("com.alvo")
public class TwitterInjestorApplication {

  public static void main(String[] args) {
    SpringApplication.run(TwitterInjestorApplication.class, args);
  }
}
