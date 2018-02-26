package com.alvo.twitterinjestor.model;

import com.twitter.hbc.core.event.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TweetStreamContainer {
  private final BlockingQueue<String> messageQueue;
  private final BlockingQueue<Event> eventQueue;

  public TweetStreamContainer(int messageQueueCapacity, int eventQueueCapacity) {
    this.messageQueue = new LinkedBlockingQueue<>(messageQueueCapacity);
    this.eventQueue = new LinkedBlockingQueue<>(eventQueueCapacity);
  }

  public BlockingQueue<String> getMessageQueue() {
    return messageQueue;
  }

  public BlockingQueue<Event> getEventQueue() {
    return eventQueue;
  }
}
