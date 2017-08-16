package com.sksamuel.elastic4s.searches.suggestion

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class PhraseSuggestionDefinition(name: String,
                                      fieldname: String,
                                      analyzer: Option[String] = None,
                                      collateParams: Map[String, AnyRef] = Map.empty,
                                      collatePrune: Option[Boolean] = None,
                                      collateQuery: Option[ScriptDefinition] = None,
                                      confidence: Option[Float] = None,
                                      forceUnigrams: Option[Boolean] = None,
                                      gramSize: Option[Int] = None,
                                      preTag: Option[String] = None,
                                      postTag: Option[String] = None,
                                      maxErrors: Option[Float] = None,
                                      realWordErrorLikelihood: Option[Float] = None,
                                      separator: Option[String] = None,
                                      tokenLimit: Option[Int] = None,
                                      size: Option[Int] = None,
                                      shardSize: Option[Int] = None,
                                      text: Option[String] = None
                                     ) extends SuggestionDefinition {

  override def analyzer(analyzer: String): PhraseSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): PhraseSuggestionDefinition = copy(text = text.some)
  override def size(size: Int): PhraseSuggestionDefinition = copy(size = size.some)
  override def shardSize(shardSize: Int): PhraseSuggestionDefinition = copy(shardSize = shardSize.some)

  //  def addCandidateGenerator(generator: CandidateGenerator): PhraseSuggestionDefinition =
  //    copy(candidateGenerator = generator.some)

  def collateParams(collateParams: Map[String, AnyRef]): PhraseSuggestionDefinition = copy(collateParams = collateParams)

  def collatePrune(collatePrune: Boolean): PhraseSuggestionDefinition = copy(collatePrune = collatePrune.some)

  def collateQuery(collateQuery: ScriptDefinition): PhraseSuggestionDefinition = copy(collateQuery = collateQuery.some)

  def collateQuery(queryType: String, fieldVariable: String, suggestionVariable: String): PhraseSuggestionDefinition = {
    val collateQueryAsJson = XContentFactory.jsonBuilder()
      .startObject()
      .startObject(queryType)
      .field(s"{{$fieldVariable}}", s"{{$suggestionVariable}}")
      .endObject()
      .endObject()
      .string()

    val template = ScriptDefinition(collateQueryAsJson)
    collateQuery(template)
  }

  def confidence(c: Float): PhraseSuggestionDefinition = copy(confidence = c.some)

  def forceUnigrams(forceUnigrams: Boolean): PhraseSuggestionDefinition = copy(forceUnigrams = forceUnigrams.some)

  def gramSize(gramSize: Int): PhraseSuggestionDefinition = copy(gramSize = gramSize.some)

  def highlight(gramSize: Int): PhraseSuggestionDefinition = copy(gramSize = gramSize.some)

  def maxErrors(f: Float): PhraseSuggestionDefinition = copy(maxErrors = f.some)

  def realWordErrorLikelihood(f: Float): PhraseSuggestionDefinition = copy(realWordErrorLikelihood = f.some)

  def separator(str: String): PhraseSuggestionDefinition = copy(separator = str.some)

  //  def smoothingModel(smoothingModel: SmoothingModel): PhraseSuggestionDefinition =
  //    copy(smoothingModel = smoothingModel.some)

  def tokenLimit(tokenLimit: Int): PhraseSuggestionDefinition = copy(tokenLimit = tokenLimit.some)


}
