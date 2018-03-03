package com.alvo.twitteringestor.streaming;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.model.TweetStreamContainer;
import com.alvo.twitteringestor.translator.TweetTranslatorService;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;

@Service
public class TweetFilteringStreamService implements StreamService<Tweet, StatusesFilterEndpoint> {

  private final Client streamingClient;
  private final TweetStreamContainer container;
  private final TweetTranslatorService translator;
  private final Authentication authentication;

  @Autowired
  public TweetFilteringStreamService(TweetTranslatorService translator,
                                     TweetStreamContainer container,
                                     Authentication authentication) {
    this.container = container;
    this.translator = translator;
    this.authentication = authentication;
    this.streamingClient = createClient();
  }

  private Client createClient() {
    return new ClientBuilder()
        .name("hbc_filter_client")
        .hosts(new HttpHosts(Constants.STREAM_HOST))
        .authentication(authentication)
        .endpoint(new StatusesFilterEndpoint())
        .processor(new StringDelimitedProcessor(container.getMessageQueue()))
        .eventMessageQueue(container.getEventQueue())
        .build();
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
  public Tweet take() throws InterruptedException {
    return translator
        .from(container.getMessageQueue().take())
        .orElseThrow(() -> new InterruptedException("Error parsing tweet"));
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
