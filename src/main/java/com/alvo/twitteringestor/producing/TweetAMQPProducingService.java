package com.alvo.twitteringestor.producing;

import com.alvo.twitteringestor.model.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TweetAMQPProducingService implements TweetProducingService<Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetAMQPProducingService.class);

  @Override
  public void produce(Tweet tweet) {
    LOGGER.info("Tweet pushed to the queue");
  }
}
