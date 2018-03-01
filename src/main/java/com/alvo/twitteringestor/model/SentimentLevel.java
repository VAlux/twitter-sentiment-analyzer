package com.alvo.twitteringestor.model;

import java.util.Arrays;

public enum SentimentLevel {

  VERY_NEGATIVE(0),
  NEGATIVE(1),
  NEUTRAL(2),
  POSITIVE(3),
  VERY_POSITIVE(4),
  UNKNOWN(Integer.MIN_VALUE);

  private final int level;

  SentimentLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public static SentimentLevel fromValue(final int value) {
    return Arrays.stream(SentimentLevel.values())
        .filter(level -> level.getLevel() == value)
        .findFirst()
        .orElse(UNKNOWN);
  }
}
