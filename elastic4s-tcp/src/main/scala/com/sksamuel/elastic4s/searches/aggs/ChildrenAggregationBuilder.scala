package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.children.ChildrenAggregationBuilder

import scala.collection.JavaConverters._

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregationDefinition): ChildrenAggregationBuilder = {
    val builder = AggregationBuilders.children(agg.name, agg.childType)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
