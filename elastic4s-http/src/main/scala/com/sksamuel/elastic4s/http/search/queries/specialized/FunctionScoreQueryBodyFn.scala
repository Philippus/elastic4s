package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.funcscorer.FunctionScoreQueryDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object FunctionScoreQueryBodyFn {

  def apply(q: FunctionScoreQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("function_score")

    q.query.map(qDef => builder.rawField("query", QueryBuilderFn(qDef).bytes()))
    q.minScore.map(builder.field("min_score", _))
    q.boost.map(builder.field("boost", _))
    q.maxBoost.map(builder.field("max_boost", _))
    q.scoreMode.map(sm => builder.field("score_mode", sm.toString.toLowerCase))
    q.boostMode.map(bm => builder.field("boost_mode", bm.toString.toLowerCase))

    if (q.scorers.nonEmpty) {
      builder.startArray("functions")
      val arrayBody = new BytesArray(q.scorers.map(scorer => FilterFunctionBodyFn(scorer).string).mkString(","))
      builder.rawValue(arrayBody, XContentType.JSON)
      builder.endArray()
    }

    builder.endObject()
    builder.endObject()
    builder
  }
}
