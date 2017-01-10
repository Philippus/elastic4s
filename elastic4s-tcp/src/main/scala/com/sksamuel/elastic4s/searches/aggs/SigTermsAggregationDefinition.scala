package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude

case class SigTermsAggregationDefinition(name: String) extends AggregationDefinition {

  type B = SignificantTermsAggregationBuilder
  val builder: B = AggregationBuilders.significantTerms(name)

  def minDocCount(minDocCount: Int): this.type = {
    builder.minDocCount(minDocCount)
    this
  }

  def executionHint(regex: String): this.type = {
    builder.executionHint(regex)
    this
  }

  def size(size: Int): this.type = {
    builder.size(size)
    this
  }

  def includeExclude(include: String, exclude: String): this.type = {
    builder.includeExclude(new IncludeExclude(include, exclude))
    this
  }

  def field(field: String): this.type = {
    builder.field(field)
    this
  }

  def shardMinDocCount(shardMinDocCount: Int): this.type = {
    builder.shardMinDocCount(shardMinDocCount)
    this
  }

  def backgroundFilter(backgroundFilter: QueryDefinition): this.type = {
    builder.backgroundFilter(QueryBuilderFn(backgroundFilter))
    this
  }

  def shardSize(shardSize: Int): this.type = {
    builder.shardSize(shardSize)
    this
  }
}
