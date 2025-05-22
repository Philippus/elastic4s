package com.sksamuel.elastic4s.requests.searches.aggs.responses

import com.sksamuel.elastic4s.AggReader
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.{
  GeoDistanceAggResult,
  HistogramAggResult,
  IpRangeAggResult,
  KeyedDateRangeAggResult
}

import scala.util.Try

trait AggBucket extends HasAggregations {
  def docCount: Long
}

case class CardinalityAggResult(name: String, value: Double) extends MetricAggregation

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

case class KeyedRangeAggResult(
    name: String,
    buckets: Map[String, RangeBucket],
    private[elastic4s] val data: Map[String, Any]
) extends BucketAggregation
    with HasAggregations

object KeyedRangeAggResult {
  def apply(name: String, data: Map[String, Any]): KeyedRangeAggResult = KeyedRangeAggResult(
    name,
    data("buckets").asInstanceOf[Map[String, Map[String, Any]]].view.mapValues(RangeBucket(_)).toMap,
    data
  )
}

case class RangeBucket(
    key: Option[String],
    from: Option[Double],
    to: Option[Double],
    override val docCount: Long,
    private[elastic4s] val data: Map[String, Any]
) extends AggBucket

object RangeBucket {
  private[elastic4s] def apply(data: Map[String, Any]): RangeBucket = RangeBucket(
    data.get("key").map(_.toString),
    data.get("from").map(_.asInstanceOf[java.lang.Number].doubleValue()),
    data.get("to").map(_.asInstanceOf[java.lang.Number].doubleValue()),
    data("doc_count").asInstanceOf[java.lang.Number].longValue(),
    data
  )
}

case class SignificantTermBucket(
    key: String,
    docCount: Long,
    bgCount: Long,
    score: Double,
    private[elastic4s] val data: Map[String, Any]
) extends AggBucket with Transformable

case class SignificantTermsAggResult(name: String, buckets: Seq[SignificantTermBucket], docCount: Long, bgCount: Long)
    extends BucketAggregation

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

case class AvgAggResult(name: String, valueOpt: Option[Double], valueAsString: Option[String])
    extends MetricAggregation {
  def value: Double = valueOpt.get
}
case class SumAggResult(name: String, valueOpt: Option[Double], valueAsString: Option[String])
    extends MetricAggregation {
  def value: Double = valueOpt.get
}
case class MinAggResult(name: String, value: Option[Double], valueAsString: Option[String]) extends MetricAggregation
case class MaxAggResult(name: String, value: Option[Double], valueAsString: Option[String]) extends MetricAggregation
case class ValueCountResult(name: String, valueOpt: Option[Double])                         extends MetricAggregation {
  def value: Double = valueOpt.get
}

case class GeoBoundsAggResult(name: String, topLeft: Option[GeoPoint], bottomRight: Option[GeoPoint])
    extends MetricAggregation

case class GeoCentroidAggResult(name: String, centroid: Option[GeoPoint], count: Long) extends MetricAggregation

case class ExtendedStatsAggResult(
    name: String,
    count: Long,
    min: Double,
    max: Double,
    avg: Double,
    sum: Double,
    sumOfSquares: Double,
    variance: Double,
    stdDeviation: Double
)

case class PercentilesAggResult(name: String, values: Map[String, Double]) extends MetricAggregation

case class ChildrenAggResult(name: String, docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends HasAggregations

object ChildrenAggResult {
  def apply(name: String, data: Map[String, Any]): ChildrenAggResult = ChildrenAggResult(
    name,
    data("doc_count").toString.toLong,
    data
  )
}

case class AvgBucketAggResult(name: String, value: Double)                       extends PipelineAggregation
case class ExtendedStatsBucketAggResult(
    name: String,
    count: Long,
    min: Double,
    max: Double,
    avg: Double,
    sum: Double,
    sumOfSquares: Double,
    variance: Double,
    stdDeviation: Double,
    stdDeviationBoundsUpper: Double,
    stdDeviationBoundsLower: Double
) extends PipelineAggregation
case class MinBucketAggResult(name: String, value: Double)                       extends PipelineAggregation
case class MovFnAggResult(name: String, value: Double)                           extends PipelineAggregation
case class PercentilesBucketAggResult(name: String, values: Map[String, Double]) extends PipelineAggregation
case class SerialDiffAggResult(name: String, value: Double)                      extends PipelineAggregation
case class StatsBucketAggResult(name: String, count: Long, min: Double, max: Double, avg: Double, sum: Double)
    extends PipelineAggregation

case class NestedAggResult(name: String, private[elastic4s] val data: Map[String, Any]) extends HasAggregations

case class ReverseNestedAggResult(name: String, private[elastic4s] val data: Map[String, Any]) extends HasAggregations

case class AdjacencyMatrixBucket(
    key: String,
    override val docCount: Long,
    private[elastic4s] val data: Map[String, Any]
) extends AggBucket

case class AdjacencyMatrix(name: String, buckets: Seq[AdjacencyMatrixBucket]) extends BucketAggregation

object AdjacencyMatrix {

  implicit object AdjacencyMatrixAggSerde extends AggSerde[AdjacencyMatrix] {
    override def read(name: String, data: Map[String, Any]): AdjacencyMatrix = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): AdjacencyMatrix = AdjacencyMatrix(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      AdjacencyMatrixBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}

// parent trait for any container of aggregations - which is the top level aggregations map you can find
// in the search result, and any buckets that contain sub aggregations
trait HasAggregations extends AggResult with Transformable {

  override private[elastic4s] def data: Map[String, Any]
  private def agg(name: String): Map[String, Any] = data(name).asInstanceOf[Map[String, Any]]

  def dataAsMap: Map[String, Any] = if (data != null) data else Map.empty

  def getAgg(name: String): Option[Aggregations] = dataAsMap.get(name) match {
    case Some(agg: Map[_, _]) => Some(Aggregations(agg.asInstanceOf[Map[String, Any]]))
    case _                    => None
  }

  def contains(name: String): Boolean = data.contains(name)
  def names: Iterable[String]         = data.keys

  // bucket aggs
  def global(name: String): GlobalAggregationResult =
    GlobalAggregationResult(name, agg(name)("doc_count").toString.toLong, agg(name))

  def filter(name: String): FilterAggregationResult =
    FilterAggregationResult(name, agg(name)("doc_count").toString.toLong, agg(name))

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

  def keyedDateRange(name: String): KeyedDateRangeAggResult = KeyedDateRangeAggResult.fromData(name, agg(name))

  /** Returns an aggregation result of type T. Uses an implicit [[AggSerde]].
    */
  def result[T <: AggResult](name: String)(implicit serde: AggSerde[T]): T = serde.read(name, agg(name))

  def children(name: String): ChildrenAggResult       = ChildrenAggResult(name, agg(name))
  def geoDistance(name: String): GeoDistanceAggResult = GeoDistanceAggResult(name, agg(name))

  def ipRange(name: String): IpRangeAggResult = IpRangeAggResult(name, agg(name))

  def range(name: String): RangeAggResult                       = RangeAggResult(name, agg(name))
  def keyedRange(name: String): KeyedRangeAggResult             = KeyedRangeAggResult(name, agg(name))
  def nested(name: String): NestedAggResult                     = NestedAggResult(name, agg(name))
  def reverseNested(name: String): ReverseNestedAggResult       = ReverseNestedAggResult(name, agg(name))
  def significantTerms(name: String): SignificantTermsAggResult = SignificantTermsAggResult(name, agg(name))

  // metric aggs
  def avg(name: String): AvgAggResult = AvgAggResult(
    name,
    Option(agg(name)("value")).map(_.toString.toDouble),
    agg(name).get("value_as_string").map(_.toString)
  )

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
  def sum(name: String): SumAggResult                 = SumAggResult(
    name,
    Option(agg(name)("value")).map(_.toString.toDouble),
    agg(name).get("value_as_string").map(_.toString)
  )
  def min(name: String): MinAggResult                 = MinAggResult(
    name,
    Option(agg(name)("value")).map(_.toString.toDouble),
    agg(name).get("value_as_string").map(_.toString)
  )
  def max(name: String): MaxAggResult                 = MaxAggResult(
    name,
    Option(agg(name)("value")).map(_.toString.toDouble),
    agg(name).get("value_as_string").map(_.toString)
  )

  def percentiles(name: String): PercentilesAggResult = {
    // can be keyed, so values can be either map or list
    val values = agg(name)("values")
    val map    = values match {
      case _: Map[_, _] => values.asInstanceOf[Map[String, Double]]
      case _: List[_]   => values.asInstanceOf[List[Map[String, Double]]].map { innermap =>
          innermap("key").toString -> innermap("value")
        }.toMap
    }
    PercentilesAggResult(name, map)
  }

  def geoBounds(name: String): GeoBoundsAggResult = {
    val boundsOpt = agg(name).get("bounds").map(_.asInstanceOf[Map[String, Map[String, Double]]])
    boundsOpt match {
      case None         => GeoBoundsAggResult(name, None, None)
      case Some(bounds) =>
        val topLeft     = bounds("top_left")
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
    val count    = agg(name)("count").toString.toLong
    GeoCentroidAggResult(name, location.map(l => GeoPoint(l("lat"), l("lon"))), count)
  }

  def valueCount(name: String): ValueCountResult =
    ValueCountResult(name, Option(agg(name)("value")).map(_.toString.toDouble))

  // pipeline aggs
  def avgBucket(name: String): AvgBucketAggResult                     = AvgBucketAggResult(name, agg(name)("value").toString.toDouble)
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
  def minBucket(name: String): MinBucketAggResult                     = MinBucketAggResult(name, agg(name)("value").toString.toDouble)
  def movFn(name: String): MovFnAggResult                             = MovFnAggResult(name, agg(name)("value").toString.toDouble)
  def percentilesBucket(name: String): PercentilesBucketAggResult     =
    PercentilesBucketAggResult(name, agg(name)("values").asInstanceOf[Map[String, Double]])
  def serialDiff(name: String): SerialDiffAggResult                   = SerialDiffAggResult(name, agg(name)("value").toString.toDouble)
  def statsBucket(name: String): StatsBucketAggResult                 =
    StatsBucketAggResult(
      name,
      count = agg(name)("count").toString.toLong,
      min = Option(agg(name).getOrElse("min", 0)).getOrElse(0).toString.toDouble,
      max = Option(agg(name).getOrElse("max", 0)).getOrElse(0).toString.toDouble,
      avg = Option(agg(name).getOrElse("avg", 0)).getOrElse(0).toString.toDouble,
      sum = agg(name).getOrElse("sum", 0).toString.toDouble
    )

  def adjacencyMatrixAgg(name: String): AdjacencyMatrix =
    result[AdjacencyMatrix](name)
}

trait MetricAggregation extends AggResult {
  def name: String
}

trait BucketAggregation extends AggResult {
  def name: String
}

trait PipelineAggregation {
  def name: String
}

trait Transformable {
  private[elastic4s] def data: Map[String, Any]
  def to[T: AggReader]: T = safeTo[T].get

  def safeTo[T](implicit reader: AggReader[T]): Try[T] = {
    val json = JacksonSupport.mapper.writeValueAsString(data)
    reader.read(json)
  }
}

case class GlobalAggregationResult(name: String, docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends BucketAggregation
    with HasAggregations

case class FilterAggregationResult(name: String, docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends BucketAggregation
    with HasAggregations

case class UnnamedFilterAggregationResult(docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends HasAggregations

case class FiltersAggregationResult(
    name: String,
    aggResults: Seq[UnnamedFilterAggregationResult],
    private[elastic4s] val data: Map[String, Any]
) extends BucketAggregation
    with HasAggregations

case class KeyedFiltersAggregationResult(
    name: String,
    aggResults: Map[String, UnnamedFilterAggregationResult],
    private[elastic4s] val data: Map[String, Any]
) extends BucketAggregation
    with HasAggregations
