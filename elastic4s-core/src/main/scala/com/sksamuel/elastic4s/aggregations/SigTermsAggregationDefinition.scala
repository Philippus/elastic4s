package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsAggregationBuilder

case class SigTermsAggregationDefinition(name: String)
  extends AggregationDefinition[SigTermsAggregationDefinition, SignificantTermsAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.significantTerms(name)

  def exclude(regex: String): this.type = {
    aggregationBuilder.exclude(regex: String)
    this
  }

  def minDocCount(minDocCount: Int): this.type = {
    aggregationBuilder.minDocCount(minDocCount)
    this
  }
  def executionHint(regex: String): this.type = {
    aggregationBuilder.executionHint(regex)
    this
  }
  def size(size: Int): this.type = {
    aggregationBuilder.size(size)
    this
  }
  def include(include: String): this.type = {
    aggregationBuilder.include(include)
    this
  }
  def field(field: String): this.type = {
    aggregationBuilder.field(field)
    this
  }
  def shardMinDocCount(shardMinDocCount: Int): this.type = {
    aggregationBuilder.shardMinDocCount(shardMinDocCount)
    this
  }
  def backgroundFilter(backgroundFilter: QueryDefinition): this.type = {
    aggregationBuilder.backgroundFilter(backgroundFilter.builder)
    this
  }
  def shardSize(shardSize: Int): this.type = {
    aggregationBuilder.shardSize(shardSize)
    this
  }
}
