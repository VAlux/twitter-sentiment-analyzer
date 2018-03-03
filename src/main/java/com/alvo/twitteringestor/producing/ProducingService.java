package com.alvo.twitteringestor.producing;

public interface ProducingService<T> {
  void produce(T source);
}
