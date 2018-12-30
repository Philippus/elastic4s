package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object TermQueryBodyFn {

  def apply(t: TermQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("term")

    builder.startObject(t.field)
    t.boost.foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))
    builder.autofield("value", t.value)

    builder.endObject().endObject().endObject()
  }
}
