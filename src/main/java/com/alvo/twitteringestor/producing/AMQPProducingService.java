package com.alvo.twitteringestor.producing;

import com.alvo.twitteringestor.model.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AMQPProducingService implements ProducingService<Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AMQPProducingService.class);

  private final RabbitTemplate rabbitTemplate;
  private final FanoutExchange fanoutExchange;

  @Autowired
  public AMQPProducingService(RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.fanoutExchange = fanoutExchange;
  }

  @Override
  public void produce(Tweet tweet) {
    LOGGER.info("Tweet {} pushed to the queue", tweet);
    rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", tweet);
  }
}
