package com.alvo.twitterinjestor.config;

import com.alvo.twitterinjestor.model.TweetStreamContainer;
import com.alvo.twitterinjestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitterinjestor.processing.TweetSentimentAnalyzeProcessingService;
import com.alvo.twitterinjestor.producing.TweetAMQPProducingService;
import com.alvo.twitterinjestor.streaming.TweetSamplingStreamService;
import com.alvo.twitterinjestor.translator.TweetTranslator;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TwitterInjestorConfig {

  @Value("${social.twitter.appId}")
  private String consumerKey;
  @Value("${social.twitter.appSecret}")
  private String consumerSecret;
  @Value("${social.twitter.tokenId}")
  private String tokenId;
  @Value("${social.twitter.tokenSecret}")
  private String tokenSecret;
  @Value("${social.twitter.messageQueueCapacity}")
  private int messageQueueCapacity;
  @Value("${social.twitter.eventQueueCapacity}")
  private int eventQueueCapacity;

  @Bean
  public Authentication hbcAuthentication() {
    return new OAuth1(consumerKey, consumerSecret, tokenId, tokenSecret);
  }

  @Bean
  @Scope("prototype")
  public TweetStreamContainer twitterStreamContainer() {
    return new TweetStreamContainer(messageQueueCapacity, eventQueueCapacity);
  }

  @Bean
  public Client twitterSamplerClient(TweetStreamContainer container) {
    return new ClientBuilder()
        .name("hbc_sampler_client")
        .hosts(new HttpHosts(Constants.STREAM_HOST))
        .authentication(hbcAuthentication())
        .endpoint(new StatusesSampleEndpoint())
        .processor(new StringDelimitedProcessor(container.getMessageQueue()))
        .eventMessageQueue(container.getEventQueue())
        .build();
  }

  @Bean
  public Client twitterFilterClient(TweetStreamContainer container) {
    return new ClientBuilder()
        .name("hbc_filter_client")
        .hosts(new HttpHosts(Constants.STREAM_HOST))
        .authentication(hbcAuthentication())
        .endpoint(new StatusesFilterEndpoint())
        .processor(new StringDelimitedProcessor(container.getMessageQueue()))
        .eventMessageQueue(container.getEventQueue())
        .build();
  }

  @Bean
  public TweetTranslator tweetTranslator() {
    return new TweetTranslator();
  }

  @Bean
  public TweetSamplingStreamService tweetSamplingStreamService() {
    TweetStreamContainer container = twitterStreamContainer();
    return new TweetSamplingStreamService(container, twitterSamplerClient(container), tweetTranslator());
  }

  @Bean
  public TweetSentimentAnalyzeProcessingService sentimentAnalyzeProcessingService() {
    return new TweetSentimentAnalyzeProcessingService();
  }

  @Bean
  public TweetAMQPProducingService tweetAMQPProducingService() {
    return new TweetAMQPProducingService();
  }

  @Bean
  public TweetIngestingPipeline<TweetSamplingStreamService,
                                  TweetSentimentAnalyzeProcessingService,
                                  TweetAMQPProducingService>
  tweetSamplingSentimentAMQPPipeline() {
    return new TweetIngestingPipeline<>(
        tweetSamplingStreamService(),
        sentimentAnalyzeProcessingService(),
        tweetAMQPProducingService()
    );
  }
}
