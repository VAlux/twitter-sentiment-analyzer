package com.alvo.twitteringestor.controller;

import com.alvo.twitteringestor.model.StreamingStatus;
import com.alvo.twitteringestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitteringestor.processing.TweetSentimentAnalyzeProcessingService;
import com.alvo.twitteringestor.producing.TweetAMQPProducingService;
import com.alvo.twitteringestor.streaming.TweetSamplingStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@RestController
public class TweetStreamController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamController.class);
  private static final String STREAMING_STOPPED_MESSAGE = "status:\"streaming stopped\"";
  private static final String STREAMING_STARTED_MESSAGE = "status:\"streaming started\"";

  private final TweetIngestingPipeline<TweetSamplingStreamService,
                                       TweetSentimentAnalyzeProcessingService,
                                       TweetAMQPProducingService> pipeline;

  @Inject
  public TweetStreamController(TweetIngestingPipeline<TweetSamplingStreamService,
                                                      TweetSentimentAnalyzeProcessingService,
                                                      TweetAMQPProducingService> pipeline) {
    this.pipeline = pipeline;
  }

  @GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> startStreaming() {
    LOGGER.info(STREAMING_STARTED_MESSAGE);
    ForkJoinTask<?> streamingTask = ForkJoinTask.adapt(pipeline::invokePipeline);
    ForkJoinPool.commonPool().submit(streamingTask);
    return ResponseEntity.ok(StreamingStatus.STARTED.asJson());
  }

  @GetMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> stopStreaming() {
    LOGGER.info(STREAMING_STOPPED_MESSAGE);
    pipeline.stopPipeline();
    return ResponseEntity.ok(StreamingStatus.STOPPED.asJson());
  }
}
