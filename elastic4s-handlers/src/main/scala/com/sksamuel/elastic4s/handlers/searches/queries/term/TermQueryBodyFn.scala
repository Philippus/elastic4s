package com.sksamuel.elastic4s.handlers.searches.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.term.TermQuery

object TermQueryBodyFn {

  def apply(t: TermQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("term")

    builder.startObject(t.field)
    t.boost.foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))
    builder.autofield("value", t.value)
    t.caseInsensitive.foreach(builder.field("case_insensitive", _))

    builder.endObject().endObject().endObject()
  }
}
