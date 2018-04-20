package com.alvo.twitteringestor.config;

import com.alvo.twitteringestor.config.properties.TweetSentimentAnalyzedConfigProperties;
import com.alvo.twitteringestor.model.TweetStreamContainer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class TweetSentimentAnalyzerConfig {

  private final TweetSentimentAnalyzedConfigProperties properties;

  @Autowired
  public TweetSentimentAnalyzerConfig(TweetSentimentAnalyzedConfigProperties properties) {
    this.properties = properties;
  }

  @Bean
  public Authentication hbcAuthentication() {
    return new OAuth1(
        properties.getAppId(),
        properties.getAppSecret(),
        properties.getTokenId(),
        properties.getTokenSecret());
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
  @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
  public TweetStreamContainer tweetStreamContainer() {
    return new TweetStreamContainer(properties.getMessageQueueCapacity(), properties.getEventQueueCapacity());
  }

  @Bean
  public StanfordCoreNLP coreNLPPipeline() {
    final String annotators = properties.getCorenlpAnnotations()
        .stream()
        .collect(Collectors.joining(","));

    Properties properties = new Properties();
    properties.setProperty("annotators", annotators);
    return new StanfordCoreNLP(properties);
  }
}