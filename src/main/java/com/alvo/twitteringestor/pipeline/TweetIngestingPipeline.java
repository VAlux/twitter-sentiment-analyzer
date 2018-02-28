package com.alvo.twitteringestor.pipeline;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.processing.TweetProcessingService;
import com.alvo.twitteringestor.producing.TweetProducingService;
import com.alvo.twitteringestor.streaming.TweetStreamService;
import com.twitter.hbc.core.endpoint.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

public class TweetIngestingPipeline<
    Streaming extends TweetStreamService<Tweet, ? extends Endpoint>,
    Processing extends TweetProcessingService<Tweet, Tweet>,
    Producing extends TweetProducingService<Tweet>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetIngestingPipeline.class);

  private final Streaming streamingService;
  private final Processing processingService;
  private final Producing producingService;

  public TweetIngestingPipeline(Streaming streamingService,
                                Processing processingService,
                                Producing producingService) {
    this.streamingService = streamingService;
    this.processingService = processingService;
    this.producingService = producingService;
  }

  public void invokePipeline() {
    streamingService.start();
    while (streamingService.isStreaming()) {
      try {
        Tweet tweet = streamingService.take();
        Tweet processed = processingService.process(tweet);
        producingService.produce(processed);
        LOGGER.info(tweet.toString());
      } catch (TwitterException | InterruptedException e) {
        LOGGER.error("Pipeline operation error: {}", e.getMessage());
      }
    }
  }

  public void stopPipeline() {
    streamingService.stop();
  }
}
