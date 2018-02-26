package com.alvo.twitterinjestor.producing;

public interface TweetProducingService<T> {
  void produce(T source);
}
