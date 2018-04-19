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