package com.alvo.twitteringestor.streaming;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.model.TweetStreamContainer;
import com.alvo.twitteringestor.translator.TweetTranslatorService;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;

@Service
public class SamplingStreamService implements StreamService<Tweet, StatusesSampleEndpoint> {

  private final Client streamingClient;
  private final TweetStreamContainer container;
  private final TweetTranslatorService translator;
  private final Authentication authentication;

  @Autowired
  public SamplingStreamService(TweetStreamContainer streamContainer,
                               TweetTranslatorService translator,
                               Authentication authentication) {
    this.translator = translator;
    this.container = streamContainer;
    this.authentication = authentication;
    this.streamingClient = createClient();
  }

  private Client createClient() {
    return new ClientBuilder()
        .name("hbc_sampler_client")
        .hosts(new HttpHosts(Constants.STREAM_HOST))
        .authentication(authentication)
        .endpoint(new StatusesSampleEndpoint())
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
  public StatusesSampleEndpoint getEndpoint() {
    return ((StatusesSampleEndpoint) streamingClient.getEndpoint());
  }

  @Override
  public boolean isStreaming() {
    return !streamingClient.isDone();
  }
}
