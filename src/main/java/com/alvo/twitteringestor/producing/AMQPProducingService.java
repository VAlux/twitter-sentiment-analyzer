package com.alvo.twitteringestor.producing;

import com.alvo.twitteringestor.model.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AMQPProducingService implements ProducingService<Tweet> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AMQPProducingService.class);

  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.queue.name}")
  private String queueName;

  @Autowired
  public AMQPProducingService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void produce(Tweet tweet) {
    LOGGER.debug("Tweet {} pushed to the queue", tweet);
    rabbitTemplate.convertAndSend(queueName, tweet);
  }
}
