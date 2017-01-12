package com.sksamuel.elastic4s.searches.queries

case class SpanTermQueryDefinition(field: String,
                                   value: Any,
                                   queryName: Option[String] = None,
                                   boost: Option[Double] = None) extends SpanQueryDefinition {

  def boost(boost: Double): SpanTermQueryDefinition = copy(boost = Some(boost))
  def queryName(queryName: String): SpanTermQueryDefinition = copy(queryName = Some(queryName))
}
