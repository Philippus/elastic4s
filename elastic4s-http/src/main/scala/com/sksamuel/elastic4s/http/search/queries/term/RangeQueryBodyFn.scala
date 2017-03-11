package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.searches.queries.RangeQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object RangeQueryBodyFn {
  def apply(range: RangeQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("range")
    builder.startObject(range.field)

    range.gte.foreach(builder.field("gte", _))
    range.lte.foreach(builder.field("lte", _))
    range.includeUpper.foreach(builder.field("include_upper", _))
    range.includeLower.foreach(builder.field("include_lower", _))

    range.boost.map(_.toString).foreach(builder.field("boost", _))
    range.timeZone.foreach(builder.field("time_zone", _))
    range.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
