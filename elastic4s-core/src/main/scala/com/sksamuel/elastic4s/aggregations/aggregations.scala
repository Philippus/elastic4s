package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations._


/** @author Nicolas Yzet */
trait AbstractAggregationDefinition {
  def builder: AbstractAggregationBuilder
}


abstract class AggregationResult[T <: AbstractAggregationDefinition] {
  type Result <: Aggregation
}


object AggregationResults {


  implicit object TermsAggregationResult extends AggregationResult[TermAggregationDefinition] {
    override type Result = org.elasticsearch.search.aggregations.bucket.terms.Terms
  }


  implicit object DateHistogramAggregationResult extends AggregationResult[DateHistogramAggregation] {
    override type Result = org.elasticsearch.search.aggregations.bucket.histogram.Histogram
  }


  implicit object CountAggregationResult extends AggregationResult[ValueCountAggregationDefinition] {
    override type Result = org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount
  }


}


trait AggregationDefinition[+Self <: AggregationDefinition[Self, B], B <: AggregationBuilder[B]]
  extends AbstractAggregationDefinition {
  val aggregationBuilder: B

  def builder = aggregationBuilder

  def aggregations(it: Iterable[AbstractAggregationDefinition]): Self = {
    it.foreach { aad => aggregationBuilder.subAggregation(aad.builder) }
    this.asInstanceOf[Self]
  }

  def aggregations(a: AbstractAggregationDefinition*): Self = aggregations(a.toIterable)

  def aggs(a: AbstractAggregationDefinition*): Self = aggregations(a)

  def aggs(a: Iterable[AbstractAggregationDefinition]): Self = aggregations(a)
}
