package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.FuzzyQueryDefinition

object FuzzyQueryBodyFn {

  def apply(q: FuzzyQueryDefinition): XContentBuilder = {
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
