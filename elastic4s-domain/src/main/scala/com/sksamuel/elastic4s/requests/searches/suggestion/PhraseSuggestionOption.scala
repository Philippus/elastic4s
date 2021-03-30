package com.sksamuel.elastic4s.requests.searches.suggestion

case class PhraseSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val highlighted: Option[String] = options.get("highlighted").map(_.asInstanceOf[String])
  val score: Double = options("score").asInstanceOf[Double]
}
