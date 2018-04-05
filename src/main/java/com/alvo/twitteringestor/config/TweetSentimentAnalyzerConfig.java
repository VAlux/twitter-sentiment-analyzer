package com.alvo.twitteringestor.config;

import com.alvo.twitteringestor.pipeline.Pipeline;
import com.alvo.twitteringestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitteringestor.processing.SentimentAnalyzeProcessingService;
import com.alvo.twitteringestor.producing.AMQPProducingService;
import com.alvo.twitteringestor.streaming.TweetFilteringStreamService;
import com.alvo.twitteringestor.streaming.TweetSamplingStreamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class TweetSentimentAnalyzerConfig {

  @Value("${social.twitter.appId}")
  private String consumerKey;
  @Value("${social.twitter.appSecret}")
  private String consumerSecret;
  @Value("${social.twitter.tokenId}")
  private String tokenId;
  @Value("${social.twitter.tokenSecret}")
  private String tokenSecret;
  @Value("${corenlp.annotations}")
  private String coreNLPAnnotations;
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
  public Authentication hbcAuthentication() {
    return new OAuth1(consumerKey, consumerSecret, tokenId, tokenSecret);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
  
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
  Binding realtimeBinding(FanoutExchange fanoutExchange) {
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

  @Bean
  @Qualifier("sampling_pipeline")
  public Pipeline samplingPipeline(TweetSamplingStreamService streaming,
                                   SentimentAnalyzeProcessingService processing,
                                   AMQPProducingService producing) {
    return new TweetIngestingPipeline<>(streaming, processing, producing);
  }

  @Bean
  @Qualifier("filtering_pipeline")
  public Pipeline filteringPipeline(TweetFilteringStreamService steaming,
                                    SentimentAnalyzeProcessingService processing,
                                    AMQPProducingService producing) {
    return new TweetIngestingPipeline<>(steaming, processing, producing);
  }

  @Bean
  public StanfordCoreNLP coreNLPPipeline() {
    Properties properties = new Properties();
    properties.setProperty("annotators", coreNLPAnnotations);
    return new StanfordCoreNLP(properties);
  }
}