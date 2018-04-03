package com.alvo.twitteringestor.streaming;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.model.TweetStreamContainer;
import com.alvo.twitteringestor.translator.Translator;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

@Service
public class TweetSamplingStreamService
    extends AbstractHBCStreamService
    implements StreamService<Tweet, StatusesSampleEndpoint> {

  private final Translator<String, Tweet> translator;

  @Autowired
  public TweetSamplingStreamService(TweetStreamContainer streamContainer,
                                    Translator<String, Tweet> translator,
                                    Authentication authentication) {
    super(authentication, streamContainer);
    this.translator = translator;
  }

  @PostConstruct
  public void connect() {
    streamingClient.connect();
  }

  @Override
  protected Client createClient() {
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
    this.isActive = true;
  }

  @Override
  public void stop() {
    this.isActive = false;
  }

  @Override
  public Optional<Tweet> take() throws InterruptedException {
    if (isActive) {
      return Optional.of(
          translator
              .from(container.getMessageQueue().take())
              .orElseThrow(() -> new InterruptedException("Error parsing tweet")));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public StatusesSampleEndpoint getEndpoint() {
    return ((StatusesSampleEndpoint) streamingClient.getEndpoint());
  }

  @Override
  public boolean isStreaming() {
    return isActive;
  }

  @PreDestroy
  public void preDestroy() {
    this.streamingClient.stop();
  }
}
