package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{ConstantScoreQueryBuilder, QueryBuilders}

object ConstantScoreBuilder {
  def apply(q: ConstantScoreDefinition): ConstantScoreQueryBuilder = {
    val builder = QueryBuilders.constantScoreQuery(QueryBuilderFn(q.query))
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    builder
  }
}
