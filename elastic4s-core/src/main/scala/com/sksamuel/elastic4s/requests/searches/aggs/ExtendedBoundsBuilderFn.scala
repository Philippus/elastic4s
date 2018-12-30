package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

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
