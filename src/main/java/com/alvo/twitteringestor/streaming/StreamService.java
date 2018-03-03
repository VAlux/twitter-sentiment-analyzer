package com.alvo.twitteringestor.streaming;

import com.twitter.hbc.core.endpoint.Endpoint;

public interface StreamService<PayloadType, EndpointType extends Endpoint> {
  void start();

  void stop();

  PayloadType take() throws InterruptedException;

  EndpointType getEndpoint();

  boolean isStreaming();
}
