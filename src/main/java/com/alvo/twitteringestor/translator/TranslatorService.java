package com.alvo.twitteringestor.translator;

import java.util.Optional;

public interface TranslatorService<SourceType, TargetType> {
  Optional<TargetType> from(SourceType source);
}
