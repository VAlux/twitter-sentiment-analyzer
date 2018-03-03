package com.alvo.twitteringestor.pipeline;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.processing.ProcessingService;
import com.alvo.twitteringestor.producing.ProducingService;
import com.alvo.twitteringestor.streaming.StreamService;
import com.twitter.hbc.core.endpoint.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TweetIngestingPipeline<
    Streaming extends StreamService<Tweet, ? extends Endpoint>,
    Processing extends ProcessingService<Tweet, Tweet>,
    Producing extends ProducingService<Tweet>> implements Pipeline {

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

  @Override
  public void invokePipeline() {
    streamingService.start();
    while (streamingService.isStreaming()) {
      try {
        Tweet tweet = streamingService.take();
        Tweet processed = processingService.process(tweet);
        producingService.produce(processed);
        LOGGER.info(tweet.toString());
      } catch (InterruptedException e) {
        LOGGER.error("Pipeline operation error: {}", e.getMessage());
      }
    }
  }

  @Override
  public void stopPipeline() {
    streamingService.stop();
  }

  @Override
  public Streaming getStreamingService() {
    return streamingService;
  }

  @Override
  public Processing getProcessingService() {
    return processingService;
  }

  @Override
  public Producing getProducingService() {
    return producingService;
  }
}
