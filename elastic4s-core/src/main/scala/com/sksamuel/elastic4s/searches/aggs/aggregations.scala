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
  def builder: B

//  def aggregations(it: Iterable[AbstractAggregationDefinition]): Self = {
//    it.foreach { aad => aggregationBuilder.subAggregation(aad.builder) }
//    this.asInstanceOf[Self]
//  }
//
//  def aggregations(a: AbstractAggregationDefinition*): Self = aggregations(a.toIterable)
//
//  def aggs(a: AbstractAggregationDefinition*): Self = aggregations(a)
//
//  def aggs(a: Iterable[AbstractAggregationDefinition]): Self = aggregations(a)
}


































































































