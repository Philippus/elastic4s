package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder

import scala.collection.JavaConverters._

object FiltersAggregationBuilder {

  def apply(agg: FiltersAggregationDefinition): FiltersAggregationBuilder = {
    val builder = AggregationBuilders.filters(agg.name, agg.filters.map(QueryBuilderFn.apply).toSeq: _*)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
