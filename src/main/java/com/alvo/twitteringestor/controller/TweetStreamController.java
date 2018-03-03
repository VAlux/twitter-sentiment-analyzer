package com.alvo.twitteringestor.controller;

import com.alvo.twitteringestor.model.StreamingStatus;
import com.alvo.twitteringestor.pipeline.Pipeline;
import com.alvo.twitteringestor.streaming.StreamService;
import com.alvo.twitteringestor.streaming.TweetFilteringStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@RestController
public class TweetStreamController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamController.class);

  private Pipeline pipeline;

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

  @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> streamFilter(@RequestParam String term) {
    LOGGER.info("{} :: {}", StreamingStatus.FILTER_APPLIED.toString(), term);
    final StreamService streamingService = pipeline.getStreamingService();
    if (streamingService instanceof TweetFilteringStreamService) {
      TweetFilteringStreamService filteringService = ((TweetFilteringStreamService) streamingService);
      filteringService.getEndpoint().trackTerms(Collections.singletonList(term));
      return ResponseEntity.ok(StreamingStatus.FILTER_APPLIED.toJson());
    }
    return ResponseEntity.ok(StreamingStatus.FILTER_FAILED.toJson());
  }

  @Autowired
  @Qualifier("filtering_pipeline")
  public void setPipeline(Pipeline pipeline) {
    this.pipeline = pipeline;
  }
}
