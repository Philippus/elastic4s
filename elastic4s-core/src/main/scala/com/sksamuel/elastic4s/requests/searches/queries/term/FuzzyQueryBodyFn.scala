package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.FuzzyQuery
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object FuzzyQueryBodyFn {

  def apply(q: FuzzyQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("fuzzy")
    builder.startObject(q.field)
    builder.autofield("value", q.termValue)
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.prefixLength.foreach(builder.field("prefix_length", _))
    q.fuzziness.foreach(builder.field("fuzziness", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
