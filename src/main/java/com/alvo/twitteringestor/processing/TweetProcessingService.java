package com.alvo.twitteringestor.processing;

public interface TweetProcessingService<SourceType, ProcessedType> {
  ProcessedType process(SourceType source);
}
