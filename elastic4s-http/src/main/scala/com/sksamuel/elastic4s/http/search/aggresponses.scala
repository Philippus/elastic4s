package com.sksamuel.elastic4s.http.search

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.json.JacksonSupport

trait AggBucket extends HasAggregations {
  def docCount: Long
}

case class TermBucket(key: String,
                      override val docCount: Long,
                      private[elastic4s] val data: Map[String, AnyRef]) extends AggBucket

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
  def apply(name: String, data: Map[String, AnyRef]): TermsAggResult = TermsAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, AnyRef]]].map { map =>
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
  def apply(name: String, data: Map[String, AnyRef]): DateHistogramAggResult = DateHistogramAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, AnyRef]]].map { map =>
      DateHistogramBucket(
        map("key_as_string").toString,
        map("key").toString.toLong,
        map("doc_count").toString.toInt,
        map
      )
    }
  )
}

case class DateHistogramBucket(date: String,
                               timestamp: Long,
                               override val docCount: Long,
                               private[elastic4s] val data: Map[String, AnyRef]) extends AggBucket

case class AvgAggResult(name: String, value: Double) extends MetricAggregation
case class SumAggResult(name: String, value: Double) extends MetricAggregation
case class MinAggResult(name: String, value: Double) extends MetricAggregation
case class MaxAggResult(name: String, value: Double) extends MetricAggregation
case class ValueCountResult(name: String, value: Double) extends MetricAggregation

case class TopHit(@JsonProperty("_index") index: String,
                  @JsonProperty("_type") `type`: String,
                  @JsonProperty("_id") id: String,
                  @JsonProperty("_score") score: Option[Double],
                  sort: Seq[String],
                  @JsonProperty("_source") source: Map[String, AnyRef]) {
  def ref = DocumentRef(index, `type`, id)
}

case class TopHitsResult(name: String,
                         total: Long,
                         @JsonProperty("max_score") maxScore: Option[Double],
                         hits: Seq[TopHit]
                        ) extends MetricAggregation

object TopHitsResult {
  def apply(name: String, data: Map[String, AnyRef]): TopHitsResult = {
    val hits = data("hits").asInstanceOf[Map[String, AnyRef]]
    val result = JacksonSupport.mapper.readValue[TopHitsResult](JacksonSupport.mapper.writeValueAsBytes(hits))
    result.copy(name = name)
  }
}

case class ChildrenAggResult(name: String,
                             docCount: Long,
                             private[elastic4s] val data: Map[String, AnyRef]) extends HasAggregations

object ChildrenAggResult {
  def apply(name: String, data: Map[String, AnyRef]): ChildrenAggResult = ChildrenAggResult(
    name,
    data("doc_count").toString.toLong,
    data
  )
}

case class Aggregations(data: Map[String, AnyRef]) extends HasAggregations

// parent trait for any container of aggregations - which is the top level aggregations map you can find
// in the search result, and any buckets that contain sub aggregations
trait HasAggregations {

  private[elastic4s] def data: Map[String, AnyRef]
  private def agg(name: String): Map[String, AnyRef] = data(name).asInstanceOf[Map[String, AnyRef]]

  def contains(name: String): Boolean = data.contains(name)
  def names: Iterable[String] = data.keys

  // bucket aggs
  def filter(name: String): FilterAggregationResult = FilterAggregationResult(name, agg(name)("doc_count").toString.toInt, agg(name))
  def dateHistogram(name: String): DateHistogramAggResult = DateHistogramAggResult(name, agg(name))
  def terms(name: String): TermsAggResult = TermsAggResult(name, agg(name))
  def children(name: String): ChildrenAggResult = ChildrenAggResult(name, agg(name))

  // metric aggs
  def avg(name: String): AvgAggResult = AvgAggResult(name, agg(name)("value").toString.toDouble)
  def cardinality(name: String): CardinalityAggResult = CardinalityAggResult(name, agg(name)("value").toString.toDouble)
  def sum(name: String): SumAggResult = SumAggResult(name, agg(name)("value").toString.toDouble)
  def min(name: String): MinAggResult = MinAggResult(name, agg(name)("value").toString.toDouble)
  def max(name: String): MaxAggResult = MaxAggResult(name, agg(name)("value").toString.toDouble)
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
                                   private[elastic4s] val data: Map[String, AnyRef]) extends BucketAggregation with HasAggregations
