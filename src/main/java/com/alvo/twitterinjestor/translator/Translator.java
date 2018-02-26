package com.alvo.twitterinjestor.translator;

import twitter4j.TwitterException;

import java.util.Optional;

@FunctionalInterface
public interface Translator<SourceType, TargetType> {
  Optional<TargetType> from(SourceType source) throws TwitterException;
}
