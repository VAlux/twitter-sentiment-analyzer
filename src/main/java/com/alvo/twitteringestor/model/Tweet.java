package com.alvo.twitteringestor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

  @JsonProperty("text")
  private String text;

  private SentimentLevel sentimentLevel;

  public Tweet() {
    this.setSentimentLevel(SentimentLevel.UNKNOWN);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public SentimentLevel getSentimentLevel() {
    return sentimentLevel;
  }

  public void setSentimentLevel(SentimentLevel sentimentLevel) {
    this.sentimentLevel = sentimentLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tweet tweet = (Tweet) o;
    return Objects.equals(getText(), tweet.getText()) &&
        getSentimentLevel() == tweet.getSentimentLevel();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getText(), getSentimentLevel());
  }

  @Override
  public String toString() {
    return "Tweet{" +
        "text='" + text + '\'' +
        ", sentimentLevel=" + sentimentLevel +
        '}';
  }
}
