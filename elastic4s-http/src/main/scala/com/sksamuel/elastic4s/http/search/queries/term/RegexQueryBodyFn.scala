package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.RegexQuery

object RegexQueryBodyFn {
  def apply(q: RegexQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("regexp")
    builder.startObject(q.field)
    builder.field("value", q.regex)
    if (q.flags.nonEmpty)
      builder.field("flags", q.flags.map(EnumConversions.regexflag).mkString("|"))
    q.maxDeterminedStates.foreach(builder.field("max_determinized_states", _))
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder
  }
}
