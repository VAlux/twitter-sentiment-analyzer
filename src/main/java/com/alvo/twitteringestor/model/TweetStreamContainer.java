package com.alvo.twitteringestor.model;

import com.twitter.hbc.core.event.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
public class TweetStreamContainer {

  @Value("${social.twitter.messageQueueCapacity}")
  private int messageQueueCapacity;
  @Value("${social.twitter.eventQueueCapacity}")
  private int eventQueueCapacity;

  private BlockingQueue<String> messageQueue;
  private BlockingQueue<Event> eventQueue;

  @PostConstruct
  public void initQueues() {
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
