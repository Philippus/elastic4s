package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder

import scala.collection.JavaConverters._

object ReverseNestedAggregationBuilder {

  def apply(agg: ReverseNestedAggregationDefinition): ReverseNestedAggregationBuilder = {
    val builder = AggregationBuilders.reverseNested(agg.name)
    agg.path.foreach(builder.path)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
