package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.term.TermsQuery

object TermsQueryBodyFn {
  def apply(t: TermsQuery[_]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    if (t.values.nonEmpty) {
      builder.startArray(t.field)
      t.values.foreach(builder.autovalue)
      builder.endArray()
    } else {
      builder.startObject(t.field)
      t.ref.foreach { ref =>
        builder.field("index", ref.index)
        builder.field("type", ref.`type`)
        builder.field("id", ref.id)
      }
      t.path.foreach(builder.field("path", _))
      t.routing.foreach(builder.field("routing", _))
      builder.endObject()
    }

    t.boost.foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }
}
