package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.FuzzyQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object FuzzyQueryBodyFn {

  def apply(q: FuzzyQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("fuzzy")
    builder.startObject(q.field)
    builder.field("value", q.termValue)
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
