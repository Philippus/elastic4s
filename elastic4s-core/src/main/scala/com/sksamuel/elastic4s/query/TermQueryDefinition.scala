package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class TermQueryDefinition(field: String, value: Any) extends QueryDefinition {

  val builder = value match {
    case str: String => QueryBuilders.termQuery(field, str)
    case iter: Iterable[Any] => QueryBuilders.termQuery(field, iter.toArray)
    case other => QueryBuilders.termQuery(field, other)
  }

  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
