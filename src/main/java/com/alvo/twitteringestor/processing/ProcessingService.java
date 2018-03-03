package com.alvo.twitteringestor.processing;

public interface ProcessingService<SourceType, ProcessedType> {
  ProcessedType process(SourceType source);
}
