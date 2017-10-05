package com.sksamuel.elastic4s.http.search

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.json.JacksonSupport

trait AggBucket extends HasAggregations {
  def docCount: Long
}

case class TermBucket(key: String,
                      override val docCount: Long,
                      private[elastic4s] val data: Map[String, Any]) extends AggBucket

case class TermsAggResult(name: String,
                          buckets: Seq[TermBucket],
                          docCountErrorUpperBound: Int,
                          otherDocCount: Int) extends BucketAggregation {

  @deprecated("use buckets", "5.2.9")
  def getBuckets: Seq[TermBucket] = buckets

  @deprecated("use bucket", "5.2.9")
  def getBucketByKey(key: String): TermBucket = bucket(key)

  def bucket(key: String): TermBucket = bucketOpt(key).get
  def bucketOpt(key: String): Option[TermBucket] = buckets.find(_.key == key)
}

object TermsAggResult {
  def apply(name: String, data: Map[String, Any]): TermsAggResult = TermsAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      TermBucket(
        map("key").toString,
        map("doc_count").toString.toInt,
        map
      )
    },
    data("doc_count_error_upper_bound").toString.toInt,
    data("sum_other_doc_count").toString.toInt
  )
}

case class CardinalityAggResult(name: String, value: Double) extends MetricAggregation

case class DateHistogramAggResult(name: String,
                                  buckets: Seq[DateHistogramBucket]) extends BucketAggregation

object DateHistogramAggResult {
  def apply(name: String, data: Map[String, Any]): DateHistogramAggResult = DateHistogramAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      DateHistogramBucket(
        map("key_as_string").toString,
        map("key").toString.toLong,
        map("doc_count").toString.toInt,
        map
      )
    }
  )
}

case class DateRangeAggResult(name: String,
                              buckets: Seq[DateRangeBucket]) extends BucketAggregation

object DateRangeAggResult {
  def apply(name: String, data: Map[String, Any]): DateRangeAggResult = DateRangeAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      DateRangeBucket(
        map.get("from").map(_.toString.toLong),
        map.get("from_as_string").map(_.toString),
        map.get("to").map(_.toString.toLong),
        map.get("to_as_string").map(_.toString),
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}

case class DateHistogramBucket(date: String,
                               timestamp: Long,
                               override val docCount: Long,
                               private[elastic4s] val data: Map[String, Any]) extends AggBucket

case class DateRangeBucket(from: Option[Long],
                           fromAsString: Option[String],
                           to: Option[Long],
                           toAsString: Option[String],
                           override val docCount: Long,
                           private[elastic4s] val data: Map[String, Any]) extends AggBucket

case class AvgAggResult(name: String, value: Double) extends MetricAggregation
case class SumAggResult(name: String, value: Double) extends MetricAggregation
case class MinAggResult(name: String, value: Option[Double]) extends MetricAggregation
case class MaxAggResult(name: String, value: Option[Double]) extends MetricAggregation
case class ValueCountResult(name: String, value: Double) extends MetricAggregation

case class ExtendedStatsAggResult(name: String,
                                  count: Long,
                                  min: Long,
                                  max: Long,
                                  avg: Long,
                                  sum: Long,
                                  sumOfSquares: Long,
                                  variance: Double,
                                  stdDeviation: Double)

case class TopHit(@JsonProperty("_index") index: String,
                  @JsonProperty("_type") `type`: String,
                  @JsonProperty("_id") id: String,
                  @JsonProperty("_score") score: Option[Double],
                  sort: Seq[String],
                  @JsonProperty("_source") source: Map[String, Any]) {
  def ref = DocumentRef(index, `type`, id)
}

case class TopHitsResult(name: String,
                         total: Long,
                         @JsonProperty("max_score") maxScore: Option[Double],
                         hits: Seq[TopHit]
                        ) extends MetricAggregation {
}

object TopHitsResult {
  def apply(name: String, data: Map[String, Any]): TopHitsResult = {
    val hits = data("hits").asInstanceOf[Map[String, Any]]
    val result = JacksonSupport.mapper.readValue[TopHitsResult](JacksonSupport.mapper.writeValueAsBytes(hits))
    result.copy(name = name)
  }
}

case class ChildrenAggResult(name: String,
                             docCount: Long,
                             private[elastic4s] val data: Map[String, Any]) extends HasAggregations

object ChildrenAggResult {
  def apply(name: String, data: Map[String, Any]): ChildrenAggResult = ChildrenAggResult(
    name,
    data("doc_count").toString.toLong,
    data
  )
}

case class Aggregations(data: Map[String, Any]) extends HasAggregations

// parent trait for any container of aggregations - which is the top level aggregations map you can find
// in the search result, and any buckets that contain sub aggregations
trait HasAggregations {

  private[elastic4s] def data: Map[String, Any]
  private def agg(name: String): Map[String, Any] = data(name).asInstanceOf[Map[String, Any]]

  def contains(name: String): Boolean = data.contains(name)
  def names: Iterable[String] = data.keys

  // bucket aggs
  def filter(name: String): FilterAggregationResult = FilterAggregationResult(name, agg(name)("doc_count").toString.toInt, agg(name))
  def dateHistogram(name: String): DateHistogramAggResult = DateHistogramAggResult(name, agg(name))
  def dateRange(name: String): DateRangeAggResult = DateRangeAggResult(name, agg(name))
  def terms(name: String): TermsAggResult = TermsAggResult(name, agg(name))
  def children(name: String): ChildrenAggResult = ChildrenAggResult(name, agg(name))

  // metric aggs
  def avg(name: String): AvgAggResult = AvgAggResult(name, agg(name)("value").toString.toDouble)

  def extendedStats(name: String): ExtendedStatsAggResult = {
    ExtendedStatsAggResult(
      name,
      count = agg(name)("count").toString.toLong,
      min = agg(name)("min").toString.toLong,
      max = agg(name)("max").toString.toLong,
      avg = agg(name)("avg").toString.toLong,
      sum = agg(name)("sum").toString.toLong,
      sumOfSquares = agg(name)("sum_of_squares").toString.toLong,
      variance = agg(name)("variance").toString.toDouble,
      stdDeviation = agg(name)("std_deviation").toString.toDouble
    )
  }

  def cardinality(name: String): CardinalityAggResult = CardinalityAggResult(name, agg(name)("value").toString.toDouble)
  def sum(name: String): SumAggResult = SumAggResult(name, agg(name)("value").toString.toDouble)
  def min(name: String): MinAggResult = MinAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))
  def max(name: String): MaxAggResult = MaxAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))
  def tophits(name: String): TopHitsResult = TopHitsResult(name, agg(name))
  def valueCount(name: String): ValueCountResult = ValueCountResult(name, agg(name)("value").toString.toDouble)
}

trait MetricAggregation {
  def name: String
}

trait BucketAggregation {
  def name: String
}

case class FilterAggregationResult(name: String,
                                   docCount: Int,
                                   private[elastic4s] val data: Map[String, Any]) extends BucketAggregation with HasAggregations
