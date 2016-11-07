package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations._

trait AggregationResult[T] {
  type Result
}

object AggregationResults {

  implicit object TermsAggregationResult extends AggregationResult[TermsAggregationDefinition] {
    override type Result = org.elasticsearch.search.aggregations.bucket.terms.Terms
  }

  implicit object DateHistogramAggregationResult extends AggregationResult[DateHistogramAggregation] {
    override type Result = org.elasticsearch.search.aggregations.bucket.histogram.Histogram
  }

  implicit object CountAggregationResult extends AggregationResult[ValueCountAggregationDefinition] {
    override type Result = org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount
  }

}

trait AggregationDefinition {

  type B <: AggregationBuilder
  val builder: B

  def subAggregation(agg: AggregationDefinition): this.type = {
    builder.subAggregation(agg.builder)
    this
  }

  def subAggregations(first: AggregationDefinition, rest: AggregationDefinition*): this.type =
    subAggregations(first +: rest)

  def subAggregations(aggs: Iterable[AggregationDefinition]): this.type = {
    aggs.foreach(subAggregation)
    this
  }
}