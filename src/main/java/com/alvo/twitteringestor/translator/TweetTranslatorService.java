package com.alvo.twitteringestor.translator;

import com.alvo.twitteringestor.model.Tweet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public final class TweetTranslatorService implements TranslatorService<String, Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetTranslatorService.class);
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
