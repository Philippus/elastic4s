package com.sksamuel.elastic4s.search.suggestions

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder.CandidateGenerator
import org.elasticsearch.search.suggest.phrase.{PhraseSuggestionBuilder, SmoothingModel}

case class PhraseSuggestionDefinition(fieldname: String,
                                      analyzer: Option[String] = None,
                                      candidateGenerator: Option[CandidateGenerator] = None,
                                      collateParams: Option[CandidateGenerator] = None,
                                      collatePrune: Option[Boolean] = None,
                                      collateQuery: Option[CandidateGenerator] = None,
                                      confidence: Option[Float] = None,
                                      forceUnigrams: Option[Boolean] = None,
                                      gramSize: Option[Int] = None,
                                      preTag: Option[String] = None,
                                      postTag: Option[String] = None,
                                      maxErrors: Option[Float] = None,
                                      realWordErrorLikelihood: Option[Float] = None,
                                      separator: Option[String] = None,
                                      smoothingModel: Option[SmoothingModel] = None,
                                      tokenLimit: Option[Int] = None,
                                      size: Option[Int] = None,
                                      shardSize: Option[Int] = None,
                                      text: Option[String] = None
                                     ) extends SuggestionDefinition {

  type B = PhraseSuggestionBuilder

  override def builder: PhraseSuggestionBuilder = {
    val builder = SuggestBuilders.phraseSuggestion(fieldname)
    super.populate(builder)
    analyzer.foreach(builder.analyzer)
    candidateGenerator.foreach(builder.addCandidateGenerator)
    collatePrune.foreach(builder.collatePrune)
    confidence.foreach(builder.confidence)
    forceUnigrams.foreach(builder.forceUnigrams)
    gramSize.foreach(builder.gramSize)
    builder.gramSize()
    (preTag, postTag) match {
      case (Some(pre), Some(post)) => builder.highlight(pre, post)
      case _ =>
    }
    maxErrors.foreach(builder.maxErrors)
    realWordErrorLikelihood.foreach(builder.realWordErrorLikelihood)
    separator.foreach(builder.separator)
    smoothingModel.foreach(builder.smoothingModel)
    builder
  }

  override def analyzer(analyzer: String): PhraseSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): PhraseSuggestionDefinition = copy(text = text.some)
  override def size(size: Int): PhraseSuggestionDefinition = copy(size = size.some)
  override def shardSize(shardSize: Int) = copy(shardSize = shardSize.some)

  def addCandidateGenerator(generator: CandidateGenerator): PhraseSuggestionDefinition =
    copy(candidateGenerator = generator.some)

  def collatePrune(collatePrune: Boolean): PhraseSuggestionDefinition = copy(collatePrune = collatePrune.some)

  def confidence(c: Float): PhraseSuggestionDefinition = copy(confidence = c.some)

  def forceUnigrams(forceUnigrams: Boolean): PhraseSuggestionDefinition = copy(forceUnigrams = forceUnigrams.some)

  def gramSize(gramSize: Int): PhraseSuggestionDefinition = copy(gramSize = gramSize.some)

  def highlight(gramSize: Int): PhraseSuggestionDefinition = copy(gramSize = gramSize.some)

  def maxErrors(f: Float): PhraseSuggestionDefinition = copy(maxErrors = f.some)

  def realWordErrorLikelihood(f: Float): PhraseSuggestionDefinition = copy(realWordErrorLikelihood = f.some)

  def separator(str: String): PhraseSuggestionDefinition = copy(separator = str.some)

  def smoothingModel(smoothingModel: SmoothingModel): PhraseSuggestionDefinition =
    copy(smoothingModel = smoothingModel.some)

  def tokenLimit(tokenLimit: Int): PhraseSuggestionDefinition = copy(tokenLimit = tokenLimit.some)


}
