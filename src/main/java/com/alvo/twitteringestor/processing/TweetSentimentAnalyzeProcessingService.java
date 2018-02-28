package com.alvo.twitteringestor.processing;

import com.alvo.twitteringestor.model.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TweetSentimentAnalyzeProcessingService implements TweetProcessingService<Tweet, Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetSentimentAnalyzeProcessingService.class);

  @Override
  public Tweet process(Tweet source) {
    LOGGER.info("Tweet analyzed");
    return source;
  }
}
