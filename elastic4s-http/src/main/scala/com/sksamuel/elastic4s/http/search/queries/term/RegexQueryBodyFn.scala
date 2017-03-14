package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.searches.queries.RegexQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object RegexQueryBodyFn {
  def apply(q: RegexQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("regexp")
    builder.startObject(q.field)
    builder.field("value", q.regex)
    if (q.flags.nonEmpty) {
      builder.field("flags", q.flags.mkString("|"))
    }
    q.maxDeterminedStates.foreach(builder.field("max_determinized_states", _))
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
