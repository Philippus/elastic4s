package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition

object TermQueryBodyFn {

  def apply(t: TermQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("term")

    builder.startObject(t.field)
    t.boost.map(_.toString).foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))
    builder.field("value", t.value)

    builder.endObject().endObject().endObject()
  }
}
