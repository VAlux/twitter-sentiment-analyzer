package com.alvo.twitteringestor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "twitter")
public class TweetSentimentAnalyzedConfigProperties {

  private String appId;
  private String appSecret;
  private String tokenId;
  private String tokenSecret;
  private int messageQueueCapacity;
  private int eventQueueCapacity;
  private List<String> corenlpAnnotations;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  public String getTokenSecret() {
    return tokenSecret;
  }

  public void setTokenSecret(String tokenSecret) {
    this.tokenSecret = tokenSecret;
  }

  public int getMessageQueueCapacity() {
    return messageQueueCapacity;
  }

  public void setMessageQueueCapacity(int messageQueueCapacity) {
    this.messageQueueCapacity = messageQueueCapacity;
  }

  public int getEventQueueCapacity() {
    return eventQueueCapacity;
  }

  public void setEventQueueCapacity(int eventQueueCapacity) {
    this.eventQueueCapacity = eventQueueCapacity;
  }

  public List<String> getCorenlpAnnotations() {
    return corenlpAnnotations;
  }

  public void setCorenlpAnnotations(List<String> corenlpAnnotations) {
    this.corenlpAnnotations = corenlpAnnotations;
  }
}
