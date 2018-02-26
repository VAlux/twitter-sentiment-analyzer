package com.alvo.twitterinjestor.controller;

import com.alvo.twitterinjestor.pipeline.TweetIngestingPipeline;
import com.alvo.twitterinjestor.processing.TweetSentimentAnalyzeProcessingService;
import com.alvo.twitterinjestor.producing.TweetAMQPProducingService;
import com.alvo.twitterinjestor.streaming.TweetSamplingStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin2.util.PojoUtil;

import javax.inject.Inject;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@RestController
public class TweetStreamController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamController.class);

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
    ForkJoinTask<?> streamingTask = ForkJoinTask.adapt(pipeline::invokePipeline );
    ForkJoinPool.commonPool().submit(streamingTask);
    return ResponseEntity.ok(PojoUtil.toJson("streaming started"));
  }

  @GetMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> stopStreaming() {
    pipeline.stopPipeline();
    return ResponseEntity.ok(PojoUtil.toJson("Streaming stopped"));
  }
}
