package com.alvo.twitteringestor.controller;

import com.alvo.twitteringestor.model.StreamingStatus;
import com.alvo.twitteringestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitteringestor.processing.SentimentAnalyzeProcessingService;
import com.alvo.twitteringestor.producing.AMQPProducingService;
import com.alvo.twitteringestor.streaming.SamplingStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@RestController
public class TweetStreamController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamController.class);

  private final TweetIngestingPipeline<SamplingStreamService,
      SentimentAnalyzeProcessingService,
      AMQPProducingService> pipeline;

  @Autowired
  public TweetStreamController(TweetIngestingPipeline<SamplingStreamService,
      SentimentAnalyzeProcessingService,
      AMQPProducingService> pipeline) {
    this.pipeline = pipeline;
  }

  @GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> startStreaming() {
    LOGGER.info(StreamingStatus.STARTED.toString());
    ForkJoinTask<?> streamingTask = ForkJoinTask.adapt(pipeline::invokePipeline);
    ForkJoinPool.commonPool().submit(streamingTask);
    return ResponseEntity.ok(StreamingStatus.STARTED.toJson());
  }

  @GetMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> stopStreaming() {
    LOGGER.info(StreamingStatus.STOPPED.toString());
    pipeline.stopPipeline();
    return ResponseEntity.ok(StreamingStatus.STOPPED.toJson());
  }
}
