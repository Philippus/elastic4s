package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.PercolateQuery

object PercolateQueryBodyFn {

  def apply(q: PercolateQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("percolate")
    builder.field("field", q.field)
    builder.field("document_type", q.`type`)
    q.ref.foreach { ref =>
      builder.field("index", ref.index)
      builder.field("type", ref.`type`)
      builder.field("id", ref.id)
    }
    q.source.foreach { source =>
      builder.rawField("document", source)
    }
    builder.endObject()
  }
}
