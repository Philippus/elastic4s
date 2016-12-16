package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude
import org.elasticsearch.search.aggregations.bucket.terms.{Terms, TermsAggregationBuilder, TermsAggregator}
import org.elasticsearch.search.aggregations.support.ValueType
import org.elasticsearch.search.aggregations.{AggregationBuilders, Aggregator}

case class TermsAggregationDefinition(name: String) extends AggregationDefinition {

  type B = TermsAggregationBuilder
  val builder: B = AggregationBuilders.terms(name)

  def field(field: String) = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition) = {
    builder.script(script.build)
    this
  }

  def missing(missing: String) = {
    builder.missing(missing)
    this
  }

  def size(size: Int): TermsAggregationDefinition = {
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

  def valueType(valueType: ValueType): this.type = {
    builder.valueType(valueType)
    this
  }

  def executionHint(executionHint: String): this.type = {
    builder.executionHint(executionHint)
    this
  }

  def shardMinDocCount(shardMinDocCount: Long): this.type = {
    builder.shardMinDocCount(shardMinDocCount)
    this
  }

  def collectMode(collectMode: Aggregator.SubAggCollectionMode): this.type = {
    builder.collectMode(collectMode)
    this
  }

  def bucketCountThresholds(bucketCountThresholds: TermsAggregator.BucketCountThresholds): this.type = {
    builder.bucketCountThresholds(bucketCountThresholds)
    this
  }

  def order(order: Terms.Order): TermsAggregationDefinition = {
    builder.order(order)
    this
  }

  def shardSize(shardSize: Int): TermsAggregationDefinition = {
    builder.shardSize(shardSize)
    this
  }

  def includeExclude(include: String, exclude: String): TermsAggregationDefinition = {
    builder.includeExclude(new IncludeExclude(include.some.orNull, exclude.some.orNull))
    this
  }

  def includeExclude(include: Iterable[String], exclude: Iterable[String]): TermsAggregationDefinition = {
    // empty array doesn't work, has to be null
    val inc = if (include.isEmpty) null else include.toArray
    val exc = if (exclude.isEmpty) null else exclude.toArray
    builder.includeExclude(new IncludeExclude(inc, exc))
    this
  }
}
