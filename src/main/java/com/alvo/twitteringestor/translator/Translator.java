package com.alvo.twitteringestor.translator;

import java.util.Optional;

public interface Translator<SourceType, TargetType> {
  Optional<TargetType> from(SourceType source);
}
