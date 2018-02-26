package com.alvo.twitterinjestor.streaming;

import com.alvo.twitterinjestor.model.Tweet;
import com.alvo.twitterinjestor.model.TweetStreamContainer;
import com.alvo.twitterinjestor.translator.TweetTranslator;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import twitter4j.TwitterException;

import java.util.concurrent.BlockingQueue;

public class TweetFilteringTweetStreamService implements TweetStreamService<Tweet, StatusesFilterEndpoint> {

  private final TweetStreamContainer streamContainer;
  private final Client streamingClient;
  private final TweetTranslator translator;

  public TweetFilteringTweetStreamService(TweetStreamContainer streamContainer,
                                          Client streamingClient,
                                          TweetTranslator translator) {
    this.streamContainer = streamContainer;
    this.streamingClient = streamingClient;
    this.translator = translator;
  }

  public BlockingQueue<Event> getEventQueue() {
    return streamContainer.getEventQueue();
  }

  @Override
  public void start() {
    streamingClient.connect();
  }

  @Override
  public void stop() {
    streamingClient.stop();
  }

  @Override
  public Tweet take() throws TwitterException, InterruptedException {
    return translator
        .from(streamContainer.getMessageQueue().take())
        .orElseThrow(() -> new TwitterException("Error parsing tweet"));
  }

  @Override
  public StatusesFilterEndpoint getEndpoint() {
    return ((StatusesFilterEndpoint) streamingClient.getEndpoint());
  }

  @Override
  public boolean isStreaming() {
    return !streamingClient.isDone();
  }
}
