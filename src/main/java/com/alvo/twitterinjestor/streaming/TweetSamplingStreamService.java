package com.alvo.twitterinjestor.streaming;

import com.alvo.twitterinjestor.model.Tweet;
import com.alvo.twitterinjestor.model.TweetStreamContainer;
import com.alvo.twitterinjestor.translator.TweetTranslator;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import twitter4j.TwitterException;

public class TweetSamplingStreamService implements TweetStreamService<Tweet, StatusesSampleEndpoint> {

  private final TweetStreamContainer streamContainer;
  private final Client streamingClient;
  private final TweetTranslator translator;

  public TweetSamplingStreamService(TweetStreamContainer streamContainer,
                                    Client streamingClient,
                                    TweetTranslator translator) {
    this.streamContainer = streamContainer;
    this.streamingClient = streamingClient;
    this.translator = translator;
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
  public StatusesSampleEndpoint getEndpoint() {
    return ((StatusesSampleEndpoint) streamingClient.getEndpoint());
  }

  @Override
  public boolean isStreaming() {
    return !streamingClient.isDone();
  }
}
