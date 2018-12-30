package com.sksamuel.elastic4s.requests.searches

case class SuggestionResult(text: String, offset: Int, length: Int, options: Seq[Map[String, Any]]) {
  def toCompletion: CompletionSuggestionResult =
    CompletionSuggestionResult(text, offset, length, options.map(CompletionSuggestionOption))
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options.map(TermSuggestionOption))
  def toPhrase: PhraseSuggestionResult =
    PhraseSuggestionResult(text, offset, length, options.map(PhraseSuggestionOption))
}

case class PhraseSuggestionResult(text: String, offset: Int, length: Int, options: Seq[PhraseSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class PhraseSuggestionOption(private val options: Map[String, Any]) {
  val text: String                = options("text").asInstanceOf[String]
  val highlighted: Option[String] = options.get("highlighted").map(_.asInstanceOf[String])
  val score: Double               = options("score").asInstanceOf[Double]
}

case class CompletionSuggestionResult(text: String,
                                      offset: Int,
                                      length: Int,
                                      options: Seq[CompletionSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class CompletionSuggestionOption(private val options: Map[String, Any]) {
  val text: String                = options("text").asInstanceOf[String]
  val score: Double               = options("_score").asInstanceOf[Double]
  val source: Map[String, AnyRef] = options.get("_source").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty)
  val index: Option[String]       = options.get("_index").map(_.asInstanceOf[String])
  val `type`: Option[String]      = options.get("_type").map(_.asInstanceOf[String])
  val id: Option[Any]             = options.get("_id")
}

case class TermSuggestionResult(text: String, offset: Int, length: Int, options: Seq[TermSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class TermSuggestionOption(private val options: Map[String, Any]) {
  val text: String  = options("text").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
  val freq: Int     = options("freq").asInstanceOf[Int]
}
