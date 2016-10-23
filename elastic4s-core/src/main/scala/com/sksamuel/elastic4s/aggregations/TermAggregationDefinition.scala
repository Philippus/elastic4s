package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode
import org.elasticsearch.search.aggregations.bucket.terms.Terms.ValueType
import org.elasticsearch.search.aggregations.bucket.terms.{Terms, TermsAggregationBuilder}

case class TermAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[TermAggregationDefinition, TermsAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.terms(name)

  def size(size: Int): TermAggregationDefinition = {
    builder.size(size)
    this
  }

  def minDocCount(minDocCount: Int): this.type = {
    builder.minDocCount(minDocCount)
    this
  }

  def showTermDocCountError(showTermDocCountError: Boolean): this.type = {
    builder.showTermDocCountError(showTermDocCountError)
    this
  }

  def collectMode(mode: SubAggCollectionMode): this.type = {
    builder.collectMode(mode)
    this
  }

  def valueType(valueType: ValueType): this.type = {
    builder.valueType(valueType)
    this
  }

  def order(order: Terms.Order): TermAggregationDefinition = {
    builder.order(order)
    this
  }

  def shardSize(shardSize: Int): TermAggregationDefinition = {
    builder.shardSize(shardSize)
    this
  }

  def include(regex: String): TermAggregationDefinition = {
    builder.in(regex)
    this
  }

  def include(values: Array[String]): TermAggregationDefinition = {
    builder.include(values)
    this
  }

  def include(values: Array[Double]): TermAggregationDefinition = {
    builder.include(values)
    this
  }

  def include(values: Array[Long]): TermAggregationDefinition = {
    builder.include(values)
    this
  }

  def exclude(regex: String): TermAggregationDefinition = {
    builder.exclude(regex)
    this
  }

  def exclude(values: Array[String]): TermAggregationDefinition = {
    builder.exclude(values)
    this
  }

  def exclude(values: Array[Double]): TermAggregationDefinition = {
    builder.exclude(values)
    this
  }

  def exclude(values: Array[Long]): TermAggregationDefinition = {
    builder.exclude(values)
    this
  }
}
