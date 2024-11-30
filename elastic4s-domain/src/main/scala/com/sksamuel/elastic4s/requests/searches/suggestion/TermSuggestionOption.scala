package com.sksamuel.elastic4s.requests.searches.suggestion

case class TermSuggestionOption(private val options: Map[String, Any]) {
  val text: String  = options("text").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
  val freq: Int     = options("freq").asInstanceOf[Int]
}
