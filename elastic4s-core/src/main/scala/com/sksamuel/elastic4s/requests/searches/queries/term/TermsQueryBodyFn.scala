package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object TermsQueryBodyFn {
  def apply(t: TermsQuery[_]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    if (t.ref.nonEmpty) {
      builder.startObject(t.field)
      t.ref.foreach { ref =>
        builder.field("index", ref.index)
        builder.field("type", ref.`type`)
        builder.field("id", ref.id)
      }
      t.path.foreach(builder.field("path", _))
      t.routing.foreach(builder.field("routing", _))
      builder.endObject()
    } else {
      builder.startArray(t.field)
      t.values.foreach(builder.autovalue)
      builder.endArray()
    }

    t.boost.foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }
}
