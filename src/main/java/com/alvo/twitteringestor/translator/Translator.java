package com.alvo.twitteringestor.translator;

import java.util.Optional;

@FunctionalInterface
public interface Translator<SourceType, TargetType> {
  Optional<TargetType> from(SourceType source);
}
