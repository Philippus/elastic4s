package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.join.query.HasChildQueryBuilder

object HasChildQueryBuilderFn {

  def apply(q: HasChildQueryDefinition): HasChildQueryBuilder = {

    val builder = new HasChildQueryBuilder(
      q.`type`,
      QueryBuilderFn(q.query),
      EnumConversions.scoreMode(q.scoreMode)
    )

    q.boost.map(_.toFloat).foreach(builder.boost)
    q.innerHit.map(InnerHitBuilder.apply).foreach(builder.innerHit)
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder.minMaxChildren(q.minChildren.getOrElse(0), q.maxChildren.getOrElse(Integer.MAX_VALUE))
    q.queryName.foreach(builder.queryName)
    builder
  }
}
