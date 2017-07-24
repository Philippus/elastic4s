package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.DateRangeAggregation
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object DateRangeAggregationBuilder {
  def apply(agg: DateRangeAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("date_range")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.format.foreach(builder.field("format", _))
    agg.keyed.foreach(builder.field("keyed", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes, XContentType.JSON)
    }

    agg.timeZone.foreach{ tz =>
      builder.field("time_zone", tz.getID)
    }

    builder.startArray("ranges")
    agg.unboundedToRanges.foreach {
      case (keyOpt, to) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("to", to)
        builder.endObject()
    }
    agg.ranges.foreach {
      case (keyOpt, from, to) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("from", from)
        builder.field("to", to)
        builder.endObject()
    }
    agg.unboundedFromRanges.foreach {
      case (keyOpt, from) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("from", from)
        builder.endObject()
    }
    builder.endArray()

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
