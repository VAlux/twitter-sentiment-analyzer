package com.alvo.twitteringestor.config;

import com.alvo.twitteringestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitteringestor.processing.TweetSentimentAnalyzeProcessingService;
import com.alvo.twitteringestor.producing.TweetAMQPProducingService;
import com.alvo.twitteringestor.streaming.TweetSamplingStreamService;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Bean
  public Authentication hbcAuthentication() {
    return new OAuth1(consumerKey, consumerSecret, tokenId, tokenSecret);
  }

  @Bean
  @Qualifier("sampling_pipeline")
  public TweetIngestingPipeline<TweetSamplingStreamService,
                                TweetSentimentAnalyzeProcessingService,
                                TweetAMQPProducingService> samplingPipeline(TweetSamplingStreamService sampler,
                                                                            TweetSentimentAnalyzeProcessingService processing,
                                                                            TweetAMQPProducingService producing) {
    return new TweetIngestingPipeline<>(sampler, processing, producing);
  }
}