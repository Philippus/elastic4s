package com.sksamuel.elastic4s.searches.suggestions

import com.sksamuel.elastic4s.searches.suggestion.PhraseSuggestionDefinition
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder

import scala.collection.JavaConverters._

object PhraseSuggestionBuilderFn {

  def apply(sugg: PhraseSuggestionDefinition): PhraseSuggestionBuilder = {
    val builder = SuggestBuilders.phraseSuggestion(sugg.fieldname)

    sugg.analyzer.foreach(builder.analyzer)
    sugg.shardSize.foreach(builder.shardSize(_))
    sugg.size.foreach(builder.size)
    sugg.text.foreach(builder.text)

    sugg.analyzer.foreach(builder.analyzer)
    sugg.candidateGenerator.foreach(builder.addCandidateGenerator)
    builder.collateParams(sugg.collateParams.asJava)
    sugg.collatePrune.foreach(builder.collatePrune)
    sugg.collateQuery.foreach(builder.collateQuery)
    sugg.confidence.foreach(builder.confidence)
    sugg.forceUnigrams.foreach(builder.forceUnigrams)
    sugg.gramSize.foreach(builder.gramSize)
    (sugg.preTag, sugg.postTag) match {
      case (Some(pre), Some(post)) => builder.highlight(pre, post)
      case _ =>
    }
    sugg.maxErrors.foreach(builder.maxErrors)
    sugg.realWordErrorLikelihood.foreach(builder.realWordErrorLikelihood)
    sugg.separator.foreach(builder.separator)
    sugg.smoothingModel.foreach(builder.smoothingModel)
    builder
  }
}
