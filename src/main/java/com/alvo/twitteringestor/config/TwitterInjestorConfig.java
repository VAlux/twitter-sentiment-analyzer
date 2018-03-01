package com.alvo.twitteringestor.config;

import com.alvo.twitteringestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitteringestor.processing.TweetSentimentAnalyzeProcessingService;
import com.alvo.twitteringestor.producing.TweetAMQPProducingService;
import com.alvo.twitteringestor.streaming.TweetSamplingStreamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

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
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
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

  @Bean
  public StanfordCoreNLP pipeline() {
    Properties properties = new Properties();
    properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
    return new StanfordCoreNLP(properties);
  }
}