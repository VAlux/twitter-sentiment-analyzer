package com.alvo.twitteringestor.pipeline;

public interface Pipeline {
  void invokePipeline();

  void stopPipeline();
}
