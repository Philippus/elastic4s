package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{IndicesQueryBuilder, QueryBuilders}

@deprecated("search instead on the `_index` field", "5.0.0")
case class IndicesQueryDefinition(indices: Iterable[String],
                                  query: QueryDefinition)
  extends QueryDefinition {

  val builder: IndicesQueryBuilder = QueryBuilders.indicesQuery(QueryBuilderFn(query), indices.toSeq: _*)

  def noMatchQuery(query: QueryDefinition): this.type = {
    builder.noMatchQuery(QueryBuilderFn(query))
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
