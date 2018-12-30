package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.FunctionScoreQuery
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object FunctionScoreQueryBuilderFn {

  def apply(q: FunctionScoreQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("function_score")

    q.query.map(qDef => builder.rawField("query", QueryBuilderFn(qDef)))
    q.minScore.map(builder.field("min_score", _))
    q.boost.map(builder.field("boost", _))
    q.maxBoost.map(builder.field("max_boost", _))
    q.scoreMode.map(sm => builder.field("score_mode", EnumConversions.scoreMode(sm)))
    q.boostMode.map(bm => builder.field("boost_mode", EnumConversions.boostMode(bm)))

    if (q.functions.nonEmpty) {
      builder.startArray("functions")
      q.functions.foreach { function =>
        builder.rawValue(ScoreFunctionBuilderFn.apply(function))
      }
      builder.endArray()
    }

    builder.endObject()
    builder
  }
}
