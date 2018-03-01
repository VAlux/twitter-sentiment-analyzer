package com.alvo.twitteringestor.producing;

import com.alvo.twitteringestor.model.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Service
public class TweetAMQPProducingService implements TweetProducingService<Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TweetAMQPProducingService.class);

  private final RabbitTemplate rabbitTemplate;
  private final ConfigurableApplicationContext context;

  @Inject
  public TweetAMQPProducingService(RabbitTemplate rabbitTemplate, ConfigurableApplicationContext context) {
    this.rabbitTemplate = rabbitTemplate;
    this.context = context;
  }

  @Override
  public void produce(Tweet tweet) {
    LOGGER.info("Tweet {}pushed to the queue", tweet);
    rabbitTemplate.convertAndSend(tweet);
  }

  @PreDestroy
  public void destroy() {
    context.close();
  }
}
