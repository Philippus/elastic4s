package com.sksamuel.elastic4s.requests.searches.suggestion

import com.sksamuel.elastic4s.XContentFactory
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class DirectGenerator(field: String,
                           size: Option[Int] = None,
                           suggestMode: Option[String] = None,
                           maxEdits: Option[Integer] = None,
                           prefixLength: Option[Int] = None,
                           minWordLength: Option[Int] = None,
                           maxInspections: Option[Int] = None,
                           minDocFreq: Option[Float] = None,
                           maxTermFreq: Option[Float] = None,
                           preFilter: Option[String] = None,
                           postFilter: Option[String] = None)

case class PhraseSuggestion(name: String,
                            fieldname: String,
                            analyzer: Option[String] = None,
                            collateParams: Map[String, AnyRef] = Map.empty,
                            collatePrune: Option[Boolean] = None,
                            collateQuery: Option[Script] = None,
                            directGenerators: Seq[DirectGenerator] = Seq.empty,
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
                            text: Option[String] = None)
    extends Suggestion {

  override def analyzer(analyzer: String): PhraseSuggestion = copy(analyzer = analyzer.some)
  override def text(text: String): PhraseSuggestion         = copy(text = text.some)
  override def size(size: Int): PhraseSuggestion            = copy(size = size.some)
  override def shardSize(shardSize: Int): PhraseSuggestion  = copy(shardSize = shardSize.some)

  def addDirectGenerator(generator: DirectGenerator): PhraseSuggestion =
    copy(directGenerators = directGenerators ++ Seq(generator))

  def collateParams(collateParams: Map[String, AnyRef]): PhraseSuggestion =
    copy(collateParams = collateParams)

  def collatePrune(collatePrune: Boolean): PhraseSuggestion = copy(collatePrune = collatePrune.some)

  def collateQuery(collateQuery: Script): PhraseSuggestion = copy(collateQuery = collateQuery.some)

  def collateQuery(queryType: String, fieldVariable: String, suggestionVariable: String): PhraseSuggestion = {
    val collateQueryAsJson = XContentFactory
      .jsonBuilder()
      .startObject()
      .startObject(queryType)
      .field(s"{{$fieldVariable}}", s"{{$suggestionVariable}}")
      .endObject()
      .endObject()
      .string()

    val template = Script(collateQueryAsJson)
    collateQuery(template)
  }

  def confidence(c: Float): PhraseSuggestion = copy(confidence = c.some)

  def forceUnigrams(forceUnigrams: Boolean): PhraseSuggestion = copy(forceUnigrams = forceUnigrams.some)

  def gramSize(gramSize: Int): PhraseSuggestion = copy(gramSize = gramSize.some)

  def highlight(gramSize: Int): PhraseSuggestion = copy(gramSize = gramSize.some)

  def maxErrors(f: Float): PhraseSuggestion = copy(maxErrors = f.some)

  def realWordErrorLikelihood(f: Float): PhraseSuggestion = copy(realWordErrorLikelihood = f.some)

  def separator(str: String): PhraseSuggestion = copy(separator = str.some)

  //  def smoothingModel(smoothingModel: SmoothingModel): PhraseSuggestionDefinition =
  //    copy(smoothingModel = smoothingModel.some)

  def tokenLimit(tokenLimit: Int): PhraseSuggestion = copy(tokenLimit = tokenLimit.some)

}
