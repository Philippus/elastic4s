package com.sksamuel.elastic4s.fields

object AnnotatedTextField {
  val `type` = "annotated_text"
}
case class AnnotatedTextField(
    name: String,
    analyzer: Option[String] = None,
    searchAnalyzer: Option[String] = None,
    searchQuoteAnalyzer: Option[String] = None,
    copyTo: Seq[String] = Nil
) extends ElasticField {
  override def `type`: String = AnnotatedTextField.`type`

  def analyzer(name: String): AnnotatedTextField = copy(analyzer = Option(name))

  def searchAnalyzer(name: String): AnnotatedTextField = copy(searchAnalyzer = Option(name))

  def searchQuoteAnalyzer(name: String): AnnotatedTextField = copy(searchQuoteAnalyzer = Option(name))
}
