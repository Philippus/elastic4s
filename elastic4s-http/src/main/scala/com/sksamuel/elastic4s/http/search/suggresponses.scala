package com.sksamuel.elastic4s.http.search

case class SuggestionResult(text: String,
                            offset: Int,
                            length: Int,
                            options: Seq[Map[String, Any]]) {
  def toCompletion: CompletionSuggestionResult = CompletionSuggestionResult(text, offset, length, options.map(CompletionSuggestionOption))
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options.map(TermSuggestionOption))
  def toPhrase: PhraseSuggestionResult = PhraseSuggestionResult(text, offset, length, options.map(PhraseSuggestionOption))
}

case class PhraseSuggestionResult(text: String, offset: Int, length: Int, options: Seq[PhraseSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class PhraseSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val highlighted: String = options("highlighted").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
}

case class CompletionSuggestionResult(text: String, offset: Int, length: Int, options: Seq[CompletionSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class CompletionSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val index: String = options("_index").asInstanceOf[String]
  val `type`: String = options("_type").asInstanceOf[String]
  val id: AnyVal = options("_id").asInstanceOf[AnyVal]
  val score: Double = options("_score").asInstanceOf[Double]
  val source: Option[Map[String, AnyRef]] = options.get("_source").asInstanceOf[Option[Map[String, AnyRef]]]
}

case class TermSuggestionResult(text: String, offset: Int, length: Int, options: Seq[TermSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class TermSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
  val freq: Int = options("freq").asInstanceOf[Int]
}
