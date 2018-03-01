package com.alvo.twitteringestor.processing;

import com.alvo.twitteringestor.model.SentimentLevel;
import com.alvo.twitteringestor.model.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TweetSentimentAnalyzeProcessingService implements TweetProcessingService<Tweet, Tweet> {
  private final StanfordCoreNLP pipeline;

  @Inject
  public TweetSentimentAnalyzeProcessingService(StanfordCoreNLP pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public Tweet process(Tweet source) {
    final String content = source.getText();
    if (StringUtils.isNotEmpty(content)) {
      Annotation annotation = pipeline.process(content);
      final double averageSentimentLevel = annotation.get(CoreAnnotations.SentencesAnnotation.class)
          .stream()
          .mapToInt(this::getPredictedClass)
          .average()
          .orElse(Integer.MIN_VALUE);

      final int roundedSentimentLevel = ((int) Math.round(averageSentimentLevel));
      source.setSentimentLevel(SentimentLevel.fromValue(roundedSentimentLevel));
    }
    return source;
  }

  private int getPredictedClass(CoreMap map) {
    Tree tree = map.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
    return RNNCoreAnnotations.getPredictedClass(tree);
  }
}
