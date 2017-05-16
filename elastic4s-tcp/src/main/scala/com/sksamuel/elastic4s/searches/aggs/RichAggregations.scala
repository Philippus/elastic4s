package com.sksamuel.elastic4s.searches.aggs

import java.util

import org.elasticsearch.search.aggregations.bucket.histogram.Histogram
import org.elasticsearch.search.aggregations.bucket.missing.InternalMissing
import org.elasticsearch.search.aggregations.bucket.terms.{StringTerms, Terms}
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality
import org.elasticsearch.search.aggregations.metrics.max.InternalMax
import org.elasticsearch.search.aggregations.metrics.min.InternalMin
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount
import org.elasticsearch.search.aggregations.{Aggregation, Aggregations}

import scala.collection.JavaConverters._

case class RichAggregations(aggregations: Aggregations) {

  def map: Map[String, Aggregation] = aggregations.getAsMap.asScala.toMap

  def getAs[T](name: String): T = aggregations.getAsMap.get(name).asInstanceOf[T]

  def missingResult(name: String): InternalMissing = getAs[InternalMissing](name)
  def cardinalityResult(name: String): InternalCardinality = getAs[InternalCardinality](name)
  def avgResult(name: String): InternalAvg = getAs[InternalAvg](name)
  def maxResult(name: String): InternalMax = getAs[InternalMax](name)
  def sumResult(name: String): InternalSum = getAs[InternalSum](name)
  def minResult(name: String): InternalMin = getAs[InternalMin](name)
  def histogramResult(name: String): Histogram = getAs[Histogram](name)
  def termsResult(name: String): Terms = getAs[Terms](name)
  def stringTermsResult(name: String): StringTerms = getAs[StringTerms](name)
  def valueCountResult(name: String): InternalValueCount = getAs[InternalValueCount](name)
}
