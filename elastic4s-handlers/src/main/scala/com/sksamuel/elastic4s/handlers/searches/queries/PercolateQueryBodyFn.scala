package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.PercolateQuery

object PercolateQueryBodyFn {

  def apply(q: PercolateQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("percolate")
    builder.field("field", q.field)
    q.ref.foreach { ref =>
      builder.field("index", ref.index.name)
      builder.field("id", ref.id)
    }
    q.source.foreach { source =>
      builder.rawField("document", source)
    }
    builder.endObject()
  }

}
