package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder

object GlobalAggregationBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: GlobalAggregationDefinition): GlobalAggregationBuilder = {
    val builder = AggregationBuilders.global(agg.name)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
