package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{HasParentQueryBuilder, QueryBuilders}

object HasParentQueryBuilder {
  def apply(q: HasParentQueryDefinition): HasParentQueryBuilder = {
    val builder = QueryBuilders.hasParentQuery(q.`type`, QueryBuilderFn(q.query), q.score)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.innerHit.map(InnerHitBuilder.apply).foreach(builder.innerHit(_, false))
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
