package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder

case class FiltersAggregationDefinition(name: String)
  extends AggregationDefinition[FiltersAggregationDefinition, FiltersAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.filters(name)

  def filter(block: => QueryDefinition): this.type = {
    builder.filter(block.builder)
    this
  }

  def filter(key: String, block: => QueryDefinition): this.type = {
    builder.filter(key, block.builder)
    this
  }
}
