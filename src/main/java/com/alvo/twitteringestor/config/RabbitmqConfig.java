package com.alvo.twitteringestor.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitmqConfig {

  @Value("${spring.rabbitmq.host}")
  private String rabbitmqHost;
  @Value("${spring.rabbitmq.port}")
  private int rabbitmqPort;
  @Value("${spring.rabbitmq.username}")
  private String rabbitmqUsername;
  @Value("${spring.rabbitmq.password}")
  private String rabbitmqPassword;
  @Value("${rabbitmq.realtime.queue.name}")
  private String realtimeQueueName;
  @Value("${rabbitmq.dataproxy.queue.name}")
  private String dataproxyQueueName;
  @Value("${rabbitmq.fanout.exchange.name}")
  private String fanoutExchangeName;

  @Bean
  public Queue realtimeSentimentAnalyzedTweetsQueue() {
    return new Queue(realtimeQueueName);
  }

  @Bean
  public Queue dataproxySentimentAnalyzedTweetsQueue() {
    return new Queue(dataproxyQueueName);
  }

  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(fanoutExchangeName);
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
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost, rabbitmqPort);
    connectionFactory.setUsername(rabbitmqUsername);
    connectionFactory.setPassword(rabbitmqPassword);
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
