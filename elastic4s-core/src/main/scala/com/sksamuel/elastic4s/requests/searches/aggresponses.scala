package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.DocumentRef
import com.sksamuel.elastic4s.{AggReader, JacksonSupport, SourceAsContentBuilder}

import scala.util.Try

trait AggBucket extends HasAggregations {
  def docCount: Long
}

case class TermBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends AggBucket
    with Transformable

case class TermsAggResult(name: String, buckets: Seq[TermBucket], docCountErrorUpperBound: Long, otherDocCount: Long)
  extends BucketAggregation {

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
    data("doc_count_error_upper_bound").toString.toLong,
    data("sum_other_doc_count").toString.toLong
  )
}

case class CardinalityAggResult(name: String, value: Double) extends MetricAggregation

case class HistogramAggResult(name: String, buckets: Seq[HistogramBucket]) extends BucketAggregation

object HistogramAggResult {
  def apply(name: String, data: Map[String, Any]): HistogramAggResult = HistogramAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      HistogramBucket(
        map("key").toString,
        map("doc_count").toString.toInt,
        map
      )
    }
  )
}

case class HistogramBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends AggBucket

case class DateHistogramAggResult(name: String, buckets: Seq[DateHistogramBucket]) extends BucketAggregation

object DateHistogramAggResult {
  def apply(name: String, data: Map[String, Any]): DateHistogramAggResult = DateHistogramAggResult(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map("key_as_string").toString, map)
        }

      //keyed results
      case buckets: Map[_, _] =>
        buckets
          .asInstanceOf[Map[String, Any]]
          .map {
            case (key, values) =>
              mkBucket(key, values.asInstanceOf[Map[String, Any]])
          }
          .toSeq
    }
  )

  private def mkBucket(key: String, map: Map[String, Any]): DateHistogramBucket =
    DateHistogramBucket(
      key,
      map("key").toString.toLong,
      map("doc_count").toString.toInt,
      map
    )
}

case class DateHistogramBucket(date: String,
                               timestamp: Long,
                               override val docCount: Long,
                               private[elastic4s] val data: Map[String, Any])
  extends AggBucket

case class DateRangeAggResult(name: String, buckets: Seq[DateRangeBucket]) extends BucketAggregation

object DateRangeAggResult {
  private[elastic4s] def apply(name: String, data: Map[String, Any]): DateRangeAggResult = DateRangeAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map(DateRangeBucket(_))
  )
}

case class KeyedDateRangeAggResult(name: String, buckets: Map[String, DateRangeBucket]) extends BucketAggregation

object KeyedDateRangeAggResult {
  // type clash with `buckets` on apply method
  private[elastic4s] def fromData(name: String, data: Map[String, Any]): KeyedDateRangeAggResult =
    KeyedDateRangeAggResult(
      name,
      data("buckets").asInstanceOf[Map[String, Map[String, Any]]].mapValues(DateRangeBucket(_))
    )
}

case class DateRangeBucket(from: Option[String],
                           fromAsString: Option[String],
                           to: Option[String],
                           toAsString: Option[String],
                           key: Option[String],
                           override val docCount: Long,
                           private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object DateRangeBucket {
  private[elastic4s] def apply(map: Map[String, Any]): DateRangeBucket = DateRangeBucket(
    map.get("from").map(_.toString),
    map.get("from_as_string").map(_.toString),
    map.get("to").map(_.toString),
    map.get("to_as_string").map(_.toString),
    map.get("key").map(_.toString),
    map("doc_count").toString.toLong,
    map
  )
}

case class RangeAggResult(name: String, buckets: Seq[RangeBucket], private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations

object RangeAggResult {
  def apply(name: String, data: Map[String, Any]): RangeAggResult = RangeAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map(RangeBucket(_)),
    data
  )
}

case class KeyedRangeAggResult(name: String,
                               buckets: Map[String, RangeBucket],
                               private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations

object KeyedRangeAggResult {
  def apply(name: String, data: Map[String, Any]): KeyedRangeAggResult = KeyedRangeAggResult(
    name,
    data("buckets").asInstanceOf[Map[String, Map[String, Any]]].mapValues(RangeBucket(_)),
    data
  )
}

case class RangeBucket(key: Option[String],
                       from: Option[Double],
                       to: Option[Double],
                       override val docCount: Long,
                       private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object RangeBucket {
  private[elastic4s] def apply(data: Map[String, Any]): RangeBucket = RangeBucket(
    data.get("key").map(_.toString),
    data.get("from").map(_.asInstanceOf[java.lang.Number].doubleValue()),
    data.get("to").map(_.asInstanceOf[java.lang.Number].doubleValue()),
    data("doc_count").asInstanceOf[java.lang.Number].longValue(),
    data
  )
}

case class GeoDistanceAggResult(name: String, buckets: Seq[GeoDistanceBucket]) extends BucketAggregation

case class GeoDistanceBucket(key: String,
                             override val docCount: Long,
                             from: Option[Double],
                             to: Option[Double],
                             private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object GeoDistanceAggResult {
  def apply(name: String, data: Map[String, Any]): GeoDistanceAggResult = GeoDistanceAggResult(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map("key").toString, map)
        }

      //keyed results
      case buckets: Map[_, _] =>
        buckets
          .asInstanceOf[Map[String, Any]]
          .map {
            case (key, values) =>
              mkBucket(key, values.asInstanceOf[Map[String, Any]])
          }
          .toSeq
    }
  )

  private def mkBucket(key: String, map: Map[String, Any]): GeoDistanceBucket =
    GeoDistanceBucket(
      key,
      map("doc_count").toString.toLong,
      map.get("from").map(_.toString.toDouble),
      map.get("to").map(_.toString.toDouble),
      map
    )
}

case class GeoHashGridAggResult(name: String, buckets: Seq[GeoHashGridBucket]) extends BucketAggregation

case class GeoHashGridBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object GeoHashGridAggResult {
  def apply(name: String, data: Map[String, Any]): GeoHashGridAggResult = GeoHashGridAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      GeoHashGridBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}

case class IpRangeAggResult(name: String, buckets: Seq[IpRangeBucket]) extends BucketAggregation

case class IpRangeBucket(key: Option[String],
                         override val docCount: Long,
                         from: Option[String],
                         to: Option[String],
                         private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object IpRangeAggResult {
  def apply(name: String, data: Map[String, Any]): IpRangeAggResult = IpRangeAggResult(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map.get("key").map(_.toString), map)
        }

      //keyed results
      case buckets: Map[_, _] =>
        buckets
          .asInstanceOf[Map[String, Any]]
          .map {
            case (key, values) =>
              mkBucket(Some(key), values.asInstanceOf[Map[String, Any]])
          }
          .toSeq
    }
  )

  private def mkBucket(key: Option[String], map: Map[String, Any]): IpRangeBucket =
    IpRangeBucket(
      key,
      map("doc_count").toString.toLong,
      map.get("from").map(_.toString),
      map.get("to").map(_.toString),
      map
    )
}

case class SignificantTermBucket(key: String, docCount: Long, bgCount: Long, score: Double, private[elastic4s] val data: Map[String, Any]) extends AggBucket with Transformable

case class SignificantTermsAggResult(name: String, buckets: Seq[SignificantTermBucket], docCount: Long, bgCount: Long) extends BucketAggregation

object SignificantTermsAggResult {
  def apply(name: String, data: Map[String, Any]): SignificantTermsAggResult = SignificantTermsAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      SignificantTermBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map("bg_count").toString.toLong,
        map("score").toString.toDouble,
        map
      )
    },
    data("doc_count").toString.toLong,
    data("bg_count").toString.toLong
  )
}

case class AvgAggResult(name: String, valueOpt: Option[Double]) extends MetricAggregation {
  def value: Double = valueOpt.get
}
case class SumAggResult(name: String, valueOpt: Option[Double]) extends MetricAggregation {
  def value: Double = valueOpt.get
}
case class MinAggResult(name: String, value: Option[Double]) extends MetricAggregation
case class MaxAggResult(name: String, value: Option[Double]) extends MetricAggregation
case class ValueCountResult(name: String, valueOpt: Option[Double]) extends MetricAggregation {
  def value: Double = valueOpt.get
}

case class GeoBoundsAggResult(name: String, topLeft: Option[GeoPoint], bottomRight: Option[GeoPoint])
  extends MetricAggregation

case class GeoCentroidAggResult(name: String, centroid: Option[GeoPoint], count: Long) extends MetricAggregation

case class ExtendedStatsAggResult(name: String,
                                  count: Long,
                                  min: Double,
                                  max: Double,
                                  avg: Double,
                                  sum: Double,
                                  sumOfSquares: Double,
                                  variance: Double,
                                  stdDeviation: Double)

case class PercentilesAggResult(name: String, values: Map[String, Double]) extends MetricAggregation

case class TopHit(@JsonProperty("_index") index: String,
                  @JsonProperty("_type") `type`: String,
                  @JsonProperty("_id") id: String,
                  @JsonProperty("_score") score: Option[Double],
                  sort: Seq[String],
                  @JsonProperty("_source") source: Map[String, Any])
  extends Transformable {
  def ref = DocumentRef(index, `type`, id)
  override private[elastic4s] val data = source
}

case class TopHitsResult(name: String,
                         total: Total,
                         @JsonProperty("max_score") maxScore: Option[Double],
                         hits: Seq[TopHit])
  extends MetricAggregation

object TopHitsResult {
  def apply(name: String, data: Map[String, Any]): TopHitsResult = {
    val hits = data("hits").asInstanceOf[Map[String, Any]]
    val result = JacksonSupport.mapper.readValue[TopHitsResult](JacksonSupport.mapper.writeValueAsBytes(hits))
    result.copy(name = name)
  }
}

case class ChildrenAggResult(name: String, docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends HasAggregations

object ChildrenAggResult {
  def apply(name: String, data: Map[String, Any]): ChildrenAggResult = ChildrenAggResult(
    name,
    data("doc_count").toString.toLong,
    data
  )
}

case class AvgBucketAggResult(name: String, value: Double) extends PipelineAggregation
case class ExtendedStatsBucketAggResult(name: String,
                                        count: Long,
                                        min: Double,
                                        max: Double,
                                        avg: Double,
                                        sum: Double,
                                        sumOfSquares: Double,
                                        variance: Double,
                                        stdDeviation: Double,
                                        stdDeviationBoundsUpper: Double,
                                        stdDeviationBoundsLower: Double)
  extends PipelineAggregation
case class MinBucketAggResult(name: String, value: Double) extends PipelineAggregation
case class MovAvgAggResult(name: String, value: Double) extends PipelineAggregation
case class PercentilesBucketAggResult(name: String, values: Map[String, Double]) extends PipelineAggregation
case class SerialDiffAggResult(name: String, value: Double) extends PipelineAggregation
case class StatsBucketAggResult(name: String, count: Long, min: Double, max: Double, avg: Double, sum: Double)
  extends PipelineAggregation

case class NestedAggResult(name: String, private[elastic4s] val data: Map[String, Any]) extends HasAggregations

case class ReverseNestedAggResult(name: String, private[elastic4s] val data: Map[String, Any]) extends HasAggregations

case class Aggregations(data: Map[String, Any]) extends HasAggregations

// parent trait for any container of aggregations - which is the top level aggregations map you can find
// in the search result, and any buckets that contain sub aggregations
trait HasAggregations extends Transformable {

  override private[elastic4s] def data: Map[String, Any]
  private def agg(name: String): Map[String, Any] = data(name).asInstanceOf[Map[String, Any]]

  def dataAsMap: Map[String, Any] = if(data != null) data else Map.empty

  def contains(name: String): Boolean = data.contains(name)
  def names: Iterable[String] = data.keys

  // bucket aggs
  def global(name: String): GlobalAggregationResult =
    GlobalAggregationResult(name, agg(name)("doc_count").toString.toInt, agg(name))

  def filter(name: String): FilterAggregationResult =
    FilterAggregationResult(name, agg(name)("doc_count").toString.toInt, agg(name))

  def filters(name: String): FiltersAggregationResult =
    FiltersAggregationResult(
      name,
      agg(name)("buckets")
        .asInstanceOf[Seq[Map[String, Any]]]
        .map(m => UnnamedFilterAggregationResult(m("doc_count").toString.toLong, data = m)),
      agg(name)
    )

  def keyedFilters(name: String): KeyedFiltersAggregationResult =
    KeyedFiltersAggregationResult(
      name,
      agg(name)("buckets").asInstanceOf[Map[String, Map[String, Any]]].map {
        case (k, v) => k -> UnnamedFilterAggregationResult(v("doc_count").toString.toLong, data = v)
      },
      agg(name)
    )

  def histogram(name: String): HistogramAggResult = HistogramAggResult(name, agg(name))
  def dateHistogram(name: String): DateHistogramAggResult = DateHistogramAggResult(name, agg(name))
  def dateRange(name: String): DateRangeAggResult = DateRangeAggResult(name, agg(name))
  def keyedDateRange(name: String): KeyedDateRangeAggResult = KeyedDateRangeAggResult.fromData(name, agg(name))
  def terms(name: String): TermsAggResult = TermsAggResult(name, agg(name))
  def children(name: String): ChildrenAggResult = ChildrenAggResult(name, agg(name))
  def geoDistance(name: String): GeoDistanceAggResult = GeoDistanceAggResult(name, agg(name))
  def geoHashGrid(name: String): GeoHashGridAggResult = GeoHashGridAggResult(name, agg(name))
  def ipRange(name: String): IpRangeAggResult = IpRangeAggResult(name, agg(name))

  def range(name: String): RangeAggResult = RangeAggResult(name, agg(name))
  def keyedRange(name: String): KeyedRangeAggResult = KeyedRangeAggResult(name, agg(name))
  def nested(name: String): NestedAggResult = NestedAggResult(name, agg(name))
  def reverseNested(name: String): ReverseNestedAggResult = ReverseNestedAggResult(name, agg(name))
  def significantTerms(name: String): SignificantTermsAggResult = SignificantTermsAggResult(name, agg(name))

  // metric aggs
  def avg(name: String): AvgAggResult = AvgAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))

  def extendedStats(name: String): ExtendedStatsAggResult =
    ExtendedStatsAggResult(
      name,
      count = agg(name)("count").toString.toLong,
      min = agg(name)("min").toString.toDouble,
      max = agg(name)("max").toString.toDouble,
      avg = agg(name)("avg").toString.toDouble,
      sum = agg(name)("sum").toString.toDouble,
      sumOfSquares = agg(name)("sum_of_squares").toString.toDouble,
      variance = agg(name)("variance").toString.toDouble,
      stdDeviation = agg(name)("std_deviation").toString.toDouble
    )

  def cardinality(name: String): CardinalityAggResult = CardinalityAggResult(name, agg(name)("value").toString.toDouble)
  def sum(name: String): SumAggResult = SumAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))
  def min(name: String): MinAggResult = MinAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))
  def max(name: String): MaxAggResult = MaxAggResult(name, Option(agg(name)("value")).map(_.toString.toDouble))
  def percentiles(name: String): PercentilesAggResult = {
    val values = agg(name)("values").asInstanceOf[Map[String, Double]]
    PercentilesAggResult(name, values)
  }

  def geoBounds(name: String): GeoBoundsAggResult = {
    val boundsOpt = agg(name).get("bounds").map(_.asInstanceOf[Map[String, Map[String, Double]]])
    boundsOpt match {
      case None => GeoBoundsAggResult(name, None, None)
      case Some(bounds) =>
        val topLeft = bounds("top_left")
        val bottomRight = bounds("bottom_right")
        GeoBoundsAggResult(
          name,
          Some(GeoPoint(topLeft("lat"), topLeft("lon"))),
          Some(GeoPoint(bottomRight("lat"), bottomRight("lon")))
        )
    }
  }

  def geoCentroid(name: String): GeoCentroidAggResult = {
    val location = agg(name).get("location").map(_.asInstanceOf[Map[String, Double]])
    val count = agg(name)("count").toString.toLong
    GeoCentroidAggResult(name, location.map(l => GeoPoint(l("lat"), l("lon"))), count)
  }

  def tophits(name: String): TopHitsResult = TopHitsResult(name, agg(name))
  def valueCount(name: String): ValueCountResult =
    ValueCountResult(name, Option(agg(name)("value")).map(_.toString.toDouble))

  // pipeline aggs
  def avgBucket(name: String): AvgBucketAggResult = AvgBucketAggResult(name, agg(name)("value").toString.toDouble)
  def extendedStatsBucket(name: String): ExtendedStatsBucketAggResult = {
    val stdDevBounds = agg(name)("std_deviation_bounds").asInstanceOf[Map[String, Double]]
    ExtendedStatsBucketAggResult(
      name,
      count = agg(name)("count").toString.toLong,
      min = agg(name)("min").toString.toDouble,
      max = agg(name)("max").toString.toDouble,
      avg = agg(name)("avg").toString.toDouble,
      sum = agg(name)("sum").toString.toDouble,
      sumOfSquares = agg(name)("sum_of_squares").toString.toDouble,
      variance = agg(name)("variance").toString.toDouble,
      stdDeviation = agg(name)("std_deviation").toString.toDouble,
      stdDeviationBoundsUpper = stdDevBounds("upper"),
      stdDeviationBoundsLower = stdDevBounds("lower")
    )
  }
  def minBucket(name: String): MinBucketAggResult = MinBucketAggResult(name, agg(name)("value").toString.toDouble)
  def movAvg(name: String): MovAvgAggResult = MovAvgAggResult(name, agg(name)("value").toString.toDouble)
  def percentilesBucket(name: String): PercentilesBucketAggResult =
    PercentilesBucketAggResult(name, agg(name)("values").asInstanceOf[Map[String, Double]])
  def serialDiff(name: String): SerialDiffAggResult = SerialDiffAggResult(name, agg(name)("value").toString.toDouble)
  def statsBucket(name: String): StatsBucketAggResult =
    StatsBucketAggResult(
      name,
      count = agg(name)("count").toString.toLong,
      min = agg(name)("min").toString.toDouble,
      max = agg(name)("max").toString.toDouble,
      avg = agg(name)("avg").toString.toDouble,
      sum = agg(name)("sum").toString.toDouble
    )
}

trait MetricAggregation {
  def name: String
}

trait BucketAggregation {
  def name: String
}

trait PipelineAggregation {
  def name: String
}

trait Transformable {
  private[elastic4s] def data: Map[String, Any]
  def to[T: AggReader]: T = safeTo[T].get

  def safeTo[T](implicit reader: AggReader[T]): Try[T] = {
    val json = SourceAsContentBuilder(data).string()
    reader.read(json)
  }
}

case class GlobalAggregationResult(name: String, docCount: Int, private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations

case class FilterAggregationResult(name: String, docCount: Int, private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations

case class UnnamedFilterAggregationResult(docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends HasAggregations

case class FiltersAggregationResult(name: String,
                                    aggResults: Seq[UnnamedFilterAggregationResult],
                                    private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations

case class KeyedFiltersAggregationResult(name: String,
                                         aggResults: Map[String, UnnamedFilterAggregationResult],
                                         private[elastic4s] val data: Map[String, Any])
  extends BucketAggregation
    with HasAggregations
