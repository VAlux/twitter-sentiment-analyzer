package com.alvo.twitteringestor.streaming;

import com.alvo.twitteringestor.model.TweetStreamContainer;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.httpclient.auth.Authentication;

import java.util.List;

public abstract class AbstractHBCStreamService {
  protected boolean isActive;
  protected final Client streamingClient;
  protected final Authentication authentication;
  protected final TweetStreamContainer container;

  public AbstractHBCStreamService(Authentication authentication, TweetStreamContainer container) {
    this.authentication = authentication;
    this.container = container;
    this.isActive = false;
    this.streamingClient = createClient();
  }

  protected StatusesFilterEndpoint createEndpoint(List<String> languages,
                                                  List<String> terms,
                                                  List<Long> followings) {
    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    endpoint.languages(languages);
    endpoint.trackTerms(terms);
    endpoint.followings(followings);
    return endpoint;
  }

  protected abstract Client createClient();
}
