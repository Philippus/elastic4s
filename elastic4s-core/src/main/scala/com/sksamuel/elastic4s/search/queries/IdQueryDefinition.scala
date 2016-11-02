package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class IdQueryDefinition(ids: Seq[String],
                             types: Seq[String] = Nil,
                             boost: Option[Double] = None,
                             queryName: Option[String] = None) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.idsQuery(types: _*).addIds(ids: _*)
    boost.foreach(b => builder.boost(b.toFloat))
    queryName.foreach(builder.queryName)
    builder
  }

  def types(types: Iterable[String]): IdQueryDefinition = copy(types = types.toSeq)
  def types(first: String, rest: String*): IdQueryDefinition = copy(types = first +: rest)

  def queryName(name: String): IdQueryDefinition = copy(queryName = Option(name))
  def boost(boost: Double): IdQueryDefinition = copy(boost = Option(boost))
}
