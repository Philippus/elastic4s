package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{IdsQueryBuilder, QueryBuilders}

object IdQueryBuilder {
  def apply(q: IdQueryDefinition): IdsQueryBuilder = {
    val builder = QueryBuilders.idsQuery(q.types: _*).addIds(q.ids: _*)
    q.boost.foreach(b => builder.boost(b.toFloat))
    q.queryName.foreach(builder.queryName)
    builder
  }
}
