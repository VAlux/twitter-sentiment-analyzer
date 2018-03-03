package com.alvo.twitteringestor.pipeline;

import com.alvo.twitteringestor.processing.ProcessingService;
import com.alvo.twitteringestor.producing.ProducingService;
import com.alvo.twitteringestor.streaming.StreamService;

public interface Pipeline {
  void invokePipeline();

  void stopPipeline();

  StreamService getStreamingService();

  ProcessingService getProcessingService();

  ProducingService getProducingService();
}
