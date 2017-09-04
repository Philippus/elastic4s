package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder

import scala.collection.JavaConverters._

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregationDefinition): ChildrenAggregationBuilder = {
    val builder = new ChildrenAggregationBuilder(agg.name, agg.childType)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
