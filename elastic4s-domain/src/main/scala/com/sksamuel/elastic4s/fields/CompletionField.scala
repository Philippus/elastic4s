package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits._

object CompletionField {
  val `type`: String = "completion"
}
case class CompletionField(
    name: String,
    analyzer: Option[String] = None,
    boost: Option[Double] = None,
    copyTo: Seq[String] = Nil,
    index: Option[Boolean] = None,
    indexOptions: Option[String] = None,
    ignoreAbove: Option[Int] = None,
    ignoreMalformed: Option[Boolean] = None,
    maxInputLength: Option[Int] = None,
    norms: Option[Boolean] = None,
    nullValue: Option[String] = None,
    preserveSeparators: Option[Boolean] = None,
    preservePositionIncrements: Option[Boolean] = None,
    similarity: Option[String] = None,
    searchAnalyzer: Option[String] = None,
    store: Option[Boolean] = None,
    termVector: Option[String] = None,
    contexts: Seq[ContextField] = Nil,
    meta: Map[String, Any] = Map.empty
) extends ElasticField {
  override def `type`: String = CompletionField.`type`

  def analyzer(name: String): CompletionField                                          = copy(analyzer = name.some)
  def searchAnalyzer(name: String): CompletionField                                    = copy(searchAnalyzer = name.some)
  def preserveSeparators(preserveSeparators: Boolean): CompletionField                 =
    copy(preserveSeparators = preserveSeparators.some)
  def preservePositionIncrements(preservePositionIncrements: Boolean): CompletionField =
    copy(preservePositionIncrements = preservePositionIncrements.some)
  def maxInputLength(maxInputLength: Int): CompletionField                             = copy(maxInputLength = maxInputLength.some)
}
