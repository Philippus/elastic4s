package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{HasChildQueryBuilder, QueryBuilders}

object HasChildQueryBuilder {
  def apply(q: HasChildQueryDefinition): HasChildQueryBuilder = {
    val builder = QueryBuilders.hasChildQuery(q.`type`, QueryBuilderFn(q.query), org.apache.lucene.search.join.ScoreMode.valueOf(q.scoreMode))
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.innerHit.map(_.builder).foreach(builder.innerHit)
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    q.minMaxChildren.foreach { case (min, max) => builder.minMaxChildren(min, max) }
    q.queryName.foreach(builder.queryName)
    builder
  }
}
