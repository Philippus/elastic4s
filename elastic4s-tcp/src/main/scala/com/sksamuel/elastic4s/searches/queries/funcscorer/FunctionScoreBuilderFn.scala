package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder

object FunctionScoreBuilderFn {
  def apply(q: FunctionScoreQueryDefinition): FunctionScoreQueryBuilder = {

    val builder = q.query match {
      case Some(query) => new FunctionScoreQueryBuilder(QueryBuilderFn(query), q.scorers.map(FilterFunctionBuilderFn.apply).toArray)
      case _ => new FunctionScoreQueryBuilder(q.scorers.map(FilterFunctionBuilderFn.apply).toArray)
    }

    q.boost.map(_.toFloat).foreach(builder.boost)
    q.maxBoost.map(_.toFloat).foreach(builder.maxBoost)
    q.minScore.map(_.toFloat).foreach(builder.setMinScore)
    q.boostMode.foreach(builder.boostMode)
    q.scoreMode.foreach(builder.scoreMode)
    builder
  }
}
