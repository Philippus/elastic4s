package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder

object NestedAggregationBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: NestedAggregationDefinition): NestedAggregationBuilder = {

    val builder = AggregationBuilders.nested(agg.name, agg.path)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
