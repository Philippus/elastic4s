package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

@deprecated("instead search on the `_index` field")
case class IndicesQueryDefinition(indices: Iterable[String],
                                  query: QueryDefinition)
  extends QueryDefinition {

  override val builder = QueryBuilders.indicesQuery(query.builder, indices.toSeq: _*)

  def noMatchQuery(query: QueryDefinition): this.type = {
    builder.noMatchQuery(query.builder)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
