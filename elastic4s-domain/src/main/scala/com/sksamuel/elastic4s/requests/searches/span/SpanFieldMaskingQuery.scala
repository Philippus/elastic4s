package com.sksamuel.elastic4s.requests.searches.span

case class SpanFieldMaskingQuery(
    field: String,
    query: SpanQuery,
    boost: Option[Double] = None,
    queryName: Option[String] = None
) extends SpanQuery {

  def boost(boost: Double): SpanFieldMaskingQuery             = copy(boost = Option(boost))
  def queryName(queryName: String): SpanFieldMaskingQuery     = withQueryName(queryName)
  def withQueryName(queryName: String): SpanFieldMaskingQuery = copy(queryName = Option(queryName))
}
