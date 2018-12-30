package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.{MultiTermQuery, Query}
import com.sksamuel.exts.OptionImplicits._

trait SpanQuery extends Query

case class SpanMultiTermQuery(query: MultiTermQuery, boost: Option[Double] = None, queryName: Option[String] = None)
    extends SpanQuery {

  def boost(boost: Double): SpanMultiTermQuery         = copy(boost = boost.some)
  def queryName(queryName: String): SpanMultiTermQuery = copy(queryName = queryName.some)
}

case class SpanFirstQuery(query: SpanQuery, end: Int, boost: Option[Double] = None, queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): SpanFirstQuery         = copy(boost = boost.some)
  def queryName(queryName: String): SpanFirstQuery = copy(queryName = queryName.some)
}
