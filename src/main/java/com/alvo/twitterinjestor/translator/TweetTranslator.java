package com.alvo.twitterinjestor.translator;

import com.alvo.twitterinjestor.model.Tweet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public final class TweetTranslator implements Translator<String, Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetTranslator.class);
  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
  }

  @Override
  public Optional<Tweet> from(final String content) {
    try {
      return Optional.of(mapper.readValue(content, Tweet.class));
    } catch (IOException e) {
      LOGGER.error("Error translating twitter status from raw JSON: {}", e.getMessage());
      return Optional.empty();
    }
  }
}
