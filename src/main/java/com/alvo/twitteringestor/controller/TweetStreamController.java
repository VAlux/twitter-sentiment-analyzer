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

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@RestController
public class TweetStreamController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetStreamController.class);
  private static final int STREAMING_POOL_PARALLELISM_FACTOR = 1;

  private Pipeline pipeline;

  private final ForkJoinPool streamingPool;

  public TweetStreamController() {
    streamingPool = new ForkJoinPool(STREAMING_POOL_PARALLELISM_FACTOR);
  }

  @GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Object> startStreaming() {
    if (streamingPool.getRunningThreadCount() <= 0) {
      LOGGER.info(StreamingStatus.STARTED.toString());
      ForkJoinTask<?> streamingTask = ForkJoinTask.adapt(pipeline::invokePipeline);
      streamingPool.submit(streamingTask);
      return ResponseEntity.ok(StreamingStatus.STARTED.toJson());
    } else {
      return ResponseEntity
          .badRequest()
          .body("Streaming already running, consider stopping current job");
    }
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
  public ResponseEntity<Object> streamFilter(@RequestParam List<String> terms) {
    LOGGER.info("{} :: {}", StreamingStatus.FILTER_APPLIED.toString(), terms);
    final StreamService streamingService = pipeline.getStreamingService();
    if (streamingService instanceof TweetFilteringStreamService) {
      TweetFilteringStreamService filteringService = ((TweetFilteringStreamService) streamingService);
      filteringService.getEndpoint().trackTerms(terms);
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
