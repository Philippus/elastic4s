package com.sksamuel.elastic4s.requests.searches.queries.span

case class SpanTermQuery(field: String, value: Any, queryName: Option[String] = None, boost: Option[Double] = None)
    extends SpanQuery {

  def boost(boost: Double): SpanTermQuery         = copy(boost = Some(boost))
  def queryName(queryName: String): SpanTermQuery = copy(queryName = Some(queryName))
}
