package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.{QueryBuilderFn, ScoreMode}
import org.elasticsearch.index.query.{NestedQueryBuilder, QueryBuilders}

object NestedQueryBuilderFn {

  def apply(q: NestedQueryDefinition): NestedQueryBuilder = {
    val builder = QueryBuilders.nestedQuery(
      q.path,
      QueryBuilderFn(q.query),
      EnumConversions.scoreMode(q.scoreMode.getOrElse(ScoreMode.Avg))
    )
    q.boost.map(_.toFloat).map(builder.boost)
    q.inner.map(InnerHitBuilder.apply).foreach(builder.innerHit(_, false))
    q.queryName.foreach(builder.queryName)
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder
  }
}
