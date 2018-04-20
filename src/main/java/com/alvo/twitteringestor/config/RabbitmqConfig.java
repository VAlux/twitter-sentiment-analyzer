package com.alvo.twitteringestor.config;

import com.alvo.twitteringestor.config.properties.RabbitmqConfigurationProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitmqConfig {

  private final RabbitmqConfigurationProperties properties;

  @Autowired
  public RabbitmqConfig(RabbitmqConfigurationProperties properties) {
    this.properties = properties;
  }

  @Bean
  public Queue realtimeSentimentAnalyzedTweetsQueue() {
    return new Queue(properties.getRealtimeQueueName());
  }

  @Bean
  public Queue dataproxySentimentAnalyzedTweetsQueue() {
    return new Queue(properties.getDataproxyQueueName());
  }

  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(properties.getFanoutExchangeName());
  }

  @Bean
  public Binding dataproxyBinding(FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(dataproxySentimentAnalyzedTweetsQueue()).to(fanoutExchange);
  }

  @Bean
  public Binding realtimeBinding(FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(realtimeSentimentAnalyzedTweetsQueue()).to(fanoutExchange);
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory(properties.getHost(), properties.getPort());

    connectionFactory.setUsername(properties.getUsername());
    connectionFactory.setPassword(properties.getPassword());
    return connectionFactory;
  }

  @Bean
  public MessageConverter rabbitMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    rabbitTemplate.setMessageConverter(rabbitMessageConverter());
    return rabbitTemplate;
  }
}
