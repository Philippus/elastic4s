package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

@deprecated("query instead search on the `_index` field", "5.0.0")
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
