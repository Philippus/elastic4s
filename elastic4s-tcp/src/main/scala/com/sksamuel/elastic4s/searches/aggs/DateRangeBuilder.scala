package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.{ElasticDate, ScriptBuilder}
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder

object DateRangeBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: DateRangeAggregation): DateRangeAggregationBuilder = {
    val builder = AggregationBuilders.dateRange(agg.name)
    agg.missing.foreach(builder.missing)
    agg.field.foreach(builder.field)
    agg.format.foreach(builder.format)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.keyed(builder.keyed)
    agg.timeZone.foreach(builder.timeZone)

    agg.unboundedFrom.foreach {
      case (Some(key), date) => builder.addUnboundedFrom(key, date.show)
      case (None, date)      => builder.addUnboundedFrom(date.show)
    }

    agg.unboundedTo.foreach {
      case (Some(key), date) => builder.addUnboundedTo(key, date.show)
      case (None, date)      => builder.addUnboundedTo(date.show)
    }

    agg.ranges.foreach {
      case (Some(key), from: ElasticDate, to: ElasticDate) => builder.addRange(key, from.show, to.show)
      case (None, from: ElasticDate, to: ElasticDate)      => builder.addRange(from.show, to.show)
    }

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
