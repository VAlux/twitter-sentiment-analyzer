package com.alvo.twitteringestor.streaming;

import com.twitter.hbc.core.endpoint.Endpoint;

import java.util.Optional;

public interface StreamService<PayloadType, EndpointType extends Endpoint> {
  void start();

  void stop();

  Optional<PayloadType> take() throws InterruptedException;

  EndpointType getEndpoint();

  boolean isStreaming();
}
