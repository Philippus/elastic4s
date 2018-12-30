package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs._

object ExtendedBoundsBuilderFn {
  def apply(agg: ExtendedBounds): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    agg match {
      case LongExtendedBounds(min, max) =>
        builder.field("min", min)
        builder.field("max", max)

      case DoubleExtendedBounds(min, max) =>
        builder.field("min", min)
        builder.field("max", max)

      case StringExtendedBounds(min, max) =>
        builder.field("min", min)
        builder.field("max", max)

      case DateExtendedBounds(min, max) =>
        builder.field("min", min.show)
        builder.field("max", max.show)
    }
    builder.endObject()
  }
}
