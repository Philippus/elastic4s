package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder

import scala.collection.JavaConverters._

object MissingAggregationBuilder {

  def apply(agg: MissingAggregationDefinition): MissingAggregationBuilder = {
    val builder = AggregationBuilders.missing(agg.name)
    agg.field.foreach(builder.field)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
