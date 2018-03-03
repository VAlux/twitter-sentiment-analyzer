package com.alvo.twitteringestor.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

public enum StreamingStatus {
  STARTED("Streaming started!"),
  STOPPED("Streaming stopped!"),
  FILTER_APPLIED("Filter applied"),
  FILTER_FAILED("Filter failed");

  private final String status;

  StreamingStatus(String status) {
    this.status = status;
  }

  public JsonNode toJson() {
    return new TextNode(status);
  }

  @Override
  public String toString() {
    return status;
  }
}
