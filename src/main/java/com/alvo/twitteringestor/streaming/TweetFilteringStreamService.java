package com.alvo.twitteringestor.streaming;

import com.alvo.twitteringestor.model.Tweet;
import com.alvo.twitteringestor.model.TweetStreamContainer;
import com.alvo.twitteringestor.translator.TranslatorService;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TweetFilteringStreamService
    extends AbstractHBCStreamService
    implements StreamService<Tweet, StatusesFilterEndpoint> {

  private final TranslatorService<String, Tweet> translator;

  @Value("${rabbitmq.filter.default.followings}")
  private String defaultFollowings;
  @Value("${rabbitmq.filter.default.terms}")
  private String deffultTerms;

  @Autowired
  public TweetFilteringStreamService(TranslatorService<String, Tweet> translator,
                                     TweetStreamContainer container,
                                     Authentication authentication) {
    super(authentication, container);
    this.translator = translator;
  }

  @PostConstruct
  public void connect() {
    streamingClient.connect();
  }

  @Override
  protected Client createClient() {
    StatusesFilterEndpoint endpoint = createDefaultEndpoint();

    return new ClientBuilder()
        .name("hbc_filter_client")
        .hosts(new HttpHosts(Constants.STREAM_HOST))
        .authentication(authentication)
        .endpoint(endpoint)
        .processor(new StringDelimitedProcessor(container.getMessageQueue()))
        .eventMessageQueue(container.getEventQueue())
        .build();
  }

  private StatusesFilterEndpoint createDefaultEndpoint() {
    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    endpoint.languages(Collections.singletonList("en"));
    endpoint.trackTerms(Collections.singletonList(deffultTerms));

    List<Long> followings =
        Arrays.stream(defaultFollowings.split(";"))
            .map(Long::valueOf)
            .collect(Collectors.toList());

    endpoint.followings(followings);
    return endpoint;
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
  public StatusesFilterEndpoint getEndpoint() {
    return ((StatusesFilterEndpoint) streamingClient.getEndpoint());
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
