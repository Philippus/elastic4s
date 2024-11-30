package com.sksamuel.elastic4s.requests.searches.suggestion

case class CompletionSuggestionOption(private val options: Map[String, Any]) {
  val text: String                = options("text").asInstanceOf[String]
  val score: Double               = options("_score").asInstanceOf[Double]
  val source: Map[String, AnyRef] = options.get("_source").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty)
  val index: Option[String]       = options.get("_index").map(_.asInstanceOf[String])
  val `type`: Option[String]      = options.get("_type").map(_.asInstanceOf[String])
  val id: Option[Any]             = options.get("_id")
}
