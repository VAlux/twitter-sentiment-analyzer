package com.alvo.twitteringestor.translator;

import com.alvo.twitteringestor.model.Tweet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public final class TweetTranslator implements Translator<String, Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetTranslator.class);
  private final ObjectMapper mapper;

  @Autowired
  public TweetTranslator(ObjectMapper mapper) {
    this.mapper = mapper;
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
