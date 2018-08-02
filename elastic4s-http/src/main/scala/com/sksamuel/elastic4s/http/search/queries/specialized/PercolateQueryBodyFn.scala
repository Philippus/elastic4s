package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.PercolateQueryDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object PercolateQueryBodyFn {

  def apply(q: PercolateQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("percolate")
    builder.field("field", q.field)
    builder.field("document_type", q.`type`)
    q.ref.foreach { ref =>
      builder.field("index", ref.index)
      builder.field("type", ref.`type`)
      builder.field("id", ref.id)
    }
    q.source.foreach { source =>
      builder.rawField("document", new BytesArray(source), XContentType.JSON)
    }
    builder.endObject()
    builder.endObject()
  }
}
