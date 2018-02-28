package com.alvo.twitteringestor.streaming;

import com.twitter.hbc.core.endpoint.Endpoint;
import twitter4j.TwitterException;

public interface TweetStreamService<PayloadType, EndpointType extends Endpoint> {
  void start();

  void stop();

  PayloadType take() throws TwitterException, InterruptedException;

  EndpointType getEndpoint();

  boolean isStreaming();
}
