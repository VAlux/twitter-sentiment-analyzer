package com.alvo.twitteringestor.translator;

import twitter4j.TwitterException;

import java.util.Optional;

public interface TranslatorService<SourceType, TargetType> {
  Optional<TargetType> from(SourceType source) throws TwitterException;
}
