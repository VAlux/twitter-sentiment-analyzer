package com.alvo.twitteringestor.producing;

public interface TweetProducingService<T> {
  void produce(T source);
}
