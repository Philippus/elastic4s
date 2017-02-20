package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.RangeQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object RangeQueryBodyFn {
  def apply(range: RangeQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("range")
    builder.startObject(range.field)

    if (range.gte.nonEmpty) {
      builder.field("gte", range.gte.get)
    }

    if (range.lte.nonEmpty) {
      builder.field("lte", range.lte.get)
    }

    range.boost.map(_.toString).foreach(builder.field("boost", _))
    range.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
