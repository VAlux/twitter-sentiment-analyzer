package com.alvo.twitterinjestor.processing;

public interface TweetProcessingService<SourceType, ProcessedType> {
  ProcessedType process(SourceType source);
}
