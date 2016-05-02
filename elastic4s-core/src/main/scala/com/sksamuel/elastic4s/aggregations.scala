package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ScriptDefinition
import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode
import org.elasticsearch.search.aggregations._
import org.elasticsearch.search.aggregations.bucket.children.ChildrenBuilder
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGridBuilder
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramBuilder, DateHistogramInterval, Histogram, HistogramBuilder}
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder
import org.elasticsearch.search.aggregations.bucket.nested.{NestedBuilder, ReverseNestedBuilder}
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceBuilder
import org.elasticsearch.search.aggregations.bucket.range.ipv4.IPv4RangeBuilder
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsBuilder
import org.elasticsearch.search.aggregations.bucket.terms.Terms.ValueType
import org.elasticsearch.search.aggregations.bucket.terms.{Terms, TermsBuilder}
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBoundsBuilder
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidBuilder
import org.elasticsearch.search.aggregations.metrics.{MetricsAggregationBuilder, ValuesSourceMetricsAggregationBuilder}
import org.elasticsearch.search.sort.SortBuilder


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


trait MetricsAggregationDefinition[+Self <: MetricsAggregationDefinition[Self, B], B <: MetricsAggregationBuilder[B]]
  extends AbstractAggregationDefinition {
  val aggregationBuilder: B

  def builder = aggregationBuilder
}


trait ValuesSourceMetricsAggregationDefinition[+Self <: ValuesSourceMetricsAggregationDefinition[Self, B], B <: ValuesSourceMetricsAggregationBuilder[B]]
  extends MetricsAggregationDefinition[Self, B] {
  self: Self =>

  def field(field: String): Self = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition): Self = {
    builder.script(script.toJavaAPI)
    this
  }
}

trait ValuesSourceAggregationDefinition[+Self <: ValuesSourceAggregationDefinition[Self, B], B <: ValuesSourceAggregationBuilder[B]]
  extends AggregationDefinition[Self, B] {
  self: Self =>

  def field(field: String): Self = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition): Self = {
    builder.script(script.toJavaAPI)
    this
  }
}



trait CardinalityMetricsAggregationDefinition[+Self <: CardinalityMetricsAggregationDefinition[Self]]
  extends MetricsAggregationDefinition[Self, CardinalityBuilder] {

  def field(field: String): CardinalityMetricsAggregationDefinition[Self] = {
    builder.field(field)
    this
  }

  def script(script: String): CardinalityMetricsAggregationDefinition[Self] = {
    builder.script(new Script(script))
    this
  }

  def rehash(rehash: Boolean): CardinalityMetricsAggregationDefinition[Self] = {
    builder.rehash(rehash)
    this
  }

  def precisionThreshold(precisionThreshold: Long): CardinalityMetricsAggregationDefinition[Self] = {
    builder.precisionThreshold(precisionThreshold)
    this
  }
}


case class MissingAggregationDefinition(name: String)
  extends AggregationDefinition[MissingAggregationDefinition, MissingBuilder] {
  val aggregationBuilder = AggregationBuilders.missing(name)

  def field(field: String): this.type = {
    builder.field(field)
    this
  }
}


case class TermAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[TermAggregationDefinition, TermsBuilder] {

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
    builder.include(regex)
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


case class RangeAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[RangeAggregationDefinition, RangeBuilder] {
  val aggregationBuilder = AggregationBuilders.range(name)

  def range(from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(from, to)
    this
  }

  def unboundedTo(to: Double): this.type = {
    builder.addUnboundedTo(to)
    this
  }

  def unboundedTo(key: String, to: Double): this.type = {
    builder.addUnboundedTo(key, to)
    this
  }

  def unboundedFrom(from: Double): this.type = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedFrom(key: String, from: Double): this.type = {
    builder.addUnboundedFrom(key, from)
    this
  }

  def ranges(ranges: (Double, Double)*): this.type = {
    for ( range <- ranges )
      builder.addRange(range._1, range._2)
    this
  }

  def range(key: String, from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(key, from, to)
    this
  }
}


case class DateRangeAggregation(name: String) extends ValuesSourceAggregationDefinition[DateRangeAggregation, DateRangeBuilder] {

  val aggregationBuilder = AggregationBuilders.dateRange(name)

  def range(from: String, to: String): DateRangeAggregation = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: String, to: String): DateRangeAggregation = {
    builder.addRange(key, from, to)
    this
  }

  def range(from: Long, to: Long): DateRangeAggregation = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: Long, to: Long): DateRangeAggregation = {
    builder.addRange(key, from, to)
    this
  }

  def unboundedFrom(from: String): DateRangeAggregation = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedTo(to: String): DateRangeAggregation = {
    builder.addUnboundedTo(to)
    this
  }

  def unboundedFrom(key: String, from: String): DateRangeAggregation = {
    builder.addUnboundedFrom(key, from)
    this
  }

  def unboundedTo(key: String, to: String): DateRangeAggregation = {
    builder.addUnboundedTo(key, to)
    this
  }

  def format(fmt: String): DateRangeAggregation = {
    builder.format(fmt)
    this
  }
}


case class ChildrenAggregationDefinition(name: String)
  extends AggregationDefinition[ChildrenAggregationDefinition, ChildrenBuilder] {
  val aggregationBuilder = AggregationBuilders.children(name)

  def childType(childType: String): this.type = {
    builder.childType(childType)
    this
  }
}


case class HistogramAggregation(name: String) extends ValuesSourceAggregationDefinition[HistogramAggregation, HistogramBuilder] {
  val aggregationBuilder = AggregationBuilders.histogram(name)

  def interval(interval: Long): HistogramAggregation = {
    builder.interval(interval)
    this
  }

  def minDocCount(minDocCount: Long): HistogramAggregation = {
    builder.minDocCount(minDocCount)
    this
  }

  def order(order: Histogram.Order) = {
    builder.order(order)
    this
  }

  def offset(offset: Long): HistogramAggregation = {
    builder.offset(offset)
    this
  }

  def extendedBounds(min: Long, max: Long): HistogramAggregation = {
    builder.extendedBounds(min, max)
    this
  }
}


case class DateHistogramAggregation(name: String)
  extends ValuesSourceAggregationDefinition[DateHistogramAggregation, DateHistogramBuilder] {
  val aggregationBuilder = AggregationBuilders.dateHistogram(name)

  def extendedBounds(minMax: (String, String)): DateHistogramAggregation = {
    builder.extendedBounds(minMax._1, minMax._2)
    this
  }

  def interval(interval: Long): DateHistogramAggregation = {
    builder.interval(interval)
    this
  }

  def interval(interval: DateHistogramInterval): DateHistogramAggregation = {
    builder.interval(interval)
    this
  }

  def minDocCount(minDocCount: Long) = {
    builder.minDocCount(minDocCount)
    this
  }

  def timeZone(timeZone: String): this.type = {
    builder.timeZone(timeZone)
    this
  }

  def offset(offset: String) = {
    builder.offset(offset)
    this
  }

  def order(order: Histogram.Order) = {
    builder.order(order)
    this
  }

  def format(format: String) = {
    builder.format(format)
    this
  }
}


case class GeoHashGridAggregationDefinition(name: String)
  extends AggregationDefinition[GeoHashGridAggregationDefinition, GeoHashGridBuilder] {

  val aggregationBuilder = AggregationBuilders.geohashGrid(name)

  def precision(precision: Int): this.type = {
    builder.precision(precision)
    this
  }

  def field(field: String): this.type = {
    builder.field(field)
    this
  }

  def shardSize(shardSize: Int): this.type = {
    builder.shardSize(shardSize)
    this
  }
  def size(size: Int): this.type = {
    builder.size(size)
    this
  }
}


case class GeoBoundsAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[GeoBoundsAggregationDefinition, GeoBoundsBuilder] {
  val aggregationBuilder = AggregationBuilders.geoBounds(name)

  def script(script: Script): GeoBoundsAggregationDefinition = {
    aggregationBuilder.script(script)
    this
  }

  def missing(missing: String): GeoBoundsAggregationDefinition = {
    aggregationBuilder.missing(missing)
    this
  }

  def wrapLongitude(wrapLongitude: Boolean): GeoBoundsAggregationDefinition = {
    builder.wrapLongitude(wrapLongitude)
    this
  }
}


case class GeoCentroidAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[GeoCentroidAggregationDefinition, GeoCentroidBuilder] {

  val aggregationBuilder = AggregationBuilders.geoCentroid(name)

  def missing(missing: String): GeoCentroidAggregationDefinition = {
    aggregationBuilder.missing(missing)
    this
  }

  def format(format: String): GeoCentroidAggregationDefinition = {
    aggregationBuilder.format(format)
    this
  }
}


case class GeoDistanceAggregationDefinition(name: String)
  extends AggregationDefinition[GeoDistanceAggregationDefinition, GeoDistanceBuilder] {
  val aggregationBuilder = AggregationBuilders.geoDistance(name)

  def range(tuple: (Double, Double)): GeoDistanceAggregationDefinition = range(tuple._1, tuple._2)
  def range(from: Double, to: Double): GeoDistanceAggregationDefinition = {
    builder.addRange(from, to)
    this
  }

  def field(field: String): GeoDistanceAggregationDefinition = {
    builder.field(field)
    this
  }

  def geoDistance(geoDistance: GeoDistance): GeoDistanceAggregationDefinition = {
    builder.distanceType(geoDistance)
    this
  }

  def geohash(geohash: String): GeoDistanceAggregationDefinition = {
    builder.geohash(geohash)
    this
  }

  def point(lat: Double, long: Double): GeoDistanceAggregationDefinition = {
    builder.point(new GeoPoint(lat, long))
    this
  }

  def addUnboundedFrom(addUnboundedFrom: Double): GeoDistanceAggregationDefinition = {
    builder.addUnboundedFrom(addUnboundedFrom)
    this
  }

  def addUnboundedTo(addUnboundedTo: Double): GeoDistanceAggregationDefinition = {
    builder.addUnboundedTo(addUnboundedTo)
    this
  }
}


case class FilterAggregationDefinition(name: String)
  extends AggregationDefinition[FilterAggregationDefinition, FilterAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.filter(name)

  def filter(block: => QueryDefinition): this.type = {
    builder.filter(block.builder)
    this
  }
}


case class FiltersAggregationDefinition(name: String)
  extends AggregationDefinition[FiltersAggregationDefinition, FiltersAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.filters(name)

  def filter(block: => QueryDefinition): this.type = {
    builder.filter(block.builder)
    this
  }

  def filter(key: String, block: => QueryDefinition): this.type = {
    builder.filter(key, block.builder)
    this
  }
}


case class SigTermsAggregationDefinition(name: String)
  extends AggregationDefinition[SigTermsAggregationDefinition, SignificantTermsBuilder] {
  val aggregationBuilder = AggregationBuilders.significantTerms(name)
  def exclude(regex: String): this.type = {
    aggregationBuilder.exclude(regex: String)
    this
  }
  def minDocCount(minDocCount: Int): this.type = {
    aggregationBuilder.minDocCount(minDocCount)
    this
  }
  def executionHint(regex: String): this.type = {
    aggregationBuilder.executionHint(regex)
    this
  }
  def size(size: Int): this.type = {
    aggregationBuilder.size(size)
    this
  }
  def include(include: String): this.type = {
    aggregationBuilder.include(include)
    this
  }
  def field(field: String): this.type = {
    aggregationBuilder.field(field)
    this
  }
  def shardMinDocCount(shardMinDocCount: Int): this.type = {
    aggregationBuilder.shardMinDocCount(shardMinDocCount)
    this
  }
  def backgroundFilter(backgroundFilter: QueryDefinition): this.type = {
    aggregationBuilder.backgroundFilter(backgroundFilter.builder)
    this
  }
  def shardSize(shardSize: Int): this.type = {
    aggregationBuilder.shardSize(shardSize)
    this
  }
}


case class IpRangeAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[IpRangeAggregationDefinition, IPv4RangeBuilder] {

  val aggregationBuilder = AggregationBuilders.ipRange(name)

  def maskRange(key: String, mask: String): this.type = {
    builder.addMaskRange(key, mask)
    this
  }

  def maskRange(mask: String): this.type = {
    builder.addMaskRange(mask)
    this
  }

  def range(from: String, to: String): this.type = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: String, to: String): this.type = {
    builder.addRange(key, from, to)
    this
  }

  def unboundedFrom(from: String): this.type = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedTo(to: String): this.type = {
    builder.addUnboundedTo(to)
    this
  }
}


case class MinAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[MinAggregationDefinition, metrics.min.MinBuilder] {
  val aggregationBuilder = AggregationBuilders.min(name)
}


case class MaxAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[MaxAggregationDefinition, metrics.max.MaxBuilder] {
  val aggregationBuilder = AggregationBuilders.max(name)
}


case class SumAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[SumAggregationDefinition, metrics.sum.SumBuilder] {
  val aggregationBuilder = AggregationBuilders.sum(name)
}


case class AvgAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[AvgAggregationDefinition, metrics.avg.AvgBuilder] {
  val aggregationBuilder = AggregationBuilders.avg(name)
}


case class StatsAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[StatsAggregationDefinition, metrics.stats.StatsBuilder] {
  val aggregationBuilder = AggregationBuilders.stats(name)
}


case class PercentilesAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[PercentilesAggregationDefinition, metrics.percentiles.PercentilesBuilder] {
  val aggregationBuilder = AggregationBuilders.percentiles(name)

  def percents(percents: Double*): PercentilesAggregationDefinition = {
    builder.percentiles(percents: _*)
    this
  }

  def compression(compression: Double): PercentilesAggregationDefinition = {
    builder.compression(compression)
    this
  }
}


case class PercentileRanksAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[PercentileRanksAggregationDefinition, metrics.percentiles.PercentileRanksBuilder] {
  val aggregationBuilder = AggregationBuilders.percentileRanks(name)

  def percents(percents: Double*): PercentileRanksAggregationDefinition = {
    builder.percentiles(percents: _*)
    this
  }

  def compression(compression: Double): PercentileRanksAggregationDefinition = {
    builder.compression(compression)
    this
  }
}


case class ExtendedStatsAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[ExtendedStatsAggregationDefinition, metrics.stats.extended.ExtendedStatsBuilder] {
  val aggregationBuilder = AggregationBuilders.extendedStats(name)
}


case class ValueCountAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[ValueCountAggregationDefinition, metrics.valuecount.ValueCountBuilder] {
  val aggregationBuilder = AggregationBuilders.count(name)
}


case class CardinalityAggregationDefinition(name: String)
  extends CardinalityMetricsAggregationDefinition[CardinalityAggregationDefinition] {
  val aggregationBuilder = AggregationBuilders.cardinality(name)
}


case class GlobalAggregationDefinition(name: String)
  extends AggregationDefinition[GlobalAggregationDefinition, GlobalBuilder] {
  val aggregationBuilder = AggregationBuilders.global(name)
}

class ScriptedMetricAggregationDefinition(name: String) extends AbstractAggregationDefinition {

  import scala.collection.JavaConverters._

  val builder = AggregationBuilders.scriptedMetric(name)

  def initScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.initScript(script.toJavaAPI)
    this
  }

  def mapScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.mapScript(script.toJavaAPI)
    this
  }

  def combineScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.combineScript(script.toJavaAPI)
    this
  }

  def reduceScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.reduceScript(script.toJavaAPI)
    this
  }

  def params(params: Map[String, Any]): ScriptedMetricAggregationDefinition = {
    val mappedParams = FieldsMapper.mapper(params).asJava
    builder.params(mappedParams)
    this
  }
}

case class TopHitsAggregationDefinition(name: String) extends AbstractAggregationDefinition {
  val builder = AggregationBuilders.topHits(name)

  def from(from: Int): this.type = {
    builder.setFrom(from)
    this
  }

  def size(size: Int): this.type = {
    builder.setSize(size)
    this
  }

  def sort(sorts: SortDefinition*): this.type = sort2(sorts.map(_.builder): _*)
  def sort2(sorts: SortBuilder*): this.type = {
    sorts.foreach(builder.addSort)
    this
  }

  def fetchSource(includes: Array[String], excludes: Array[String]): this.type = {
    builder.setFetchSource(includes, excludes)
    this
  }

  def script(name:String,script:ScriptDefinition):this.type ={
    builder.addScriptField(name,script.toJavaAPI)
    this
  }
}


case class NestedAggregationDefinition(name: String)
  extends AggregationDefinition[NestedAggregationDefinition, NestedBuilder] {
  val aggregationBuilder = AggregationBuilders.nested(name)

  def path(path: String): NestedAggregationDefinition = {
    builder.path(path)
    this
  }
}


case class ReverseNestedAggregationDefinition(name: String)
  extends AggregationDefinition[ReverseNestedAggregationDefinition, ReverseNestedBuilder] {
  val aggregationBuilder = AggregationBuilders.reverseNested(name)

  def path(path: String): ReverseNestedAggregationDefinition = {
    builder.path(path)
    this
  }
}
