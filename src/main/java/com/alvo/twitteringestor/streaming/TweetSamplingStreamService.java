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
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;

import javax.inject.Inject;

@Service
public class TweetSamplingStreamService implements TweetStreamService<Tweet, StatusesSampleEndpoint> {

  private final Client streamingClient;
  private final TweetStreamContainer container;
  private final TweetTranslatorService translator;
  private final Authentication authentication;

  @Inject
  public TweetSamplingStreamService(TweetStreamContainer streamContainer,
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
  public Tweet take() throws TwitterException, InterruptedException {
    return translator
        .from(container.getMessageQueue().take())
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
