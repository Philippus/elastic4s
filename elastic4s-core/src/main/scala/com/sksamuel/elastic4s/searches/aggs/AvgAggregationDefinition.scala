package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder

case class AvgAggregationDefinition(name: String) extends AggregationDefinition {
  type B = AvgAggregationBuilder
  override val builder: B = AggregationBuilders.avg(name)
}
