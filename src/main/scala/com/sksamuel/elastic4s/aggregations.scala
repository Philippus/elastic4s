package com.sksamuel.elastic4s

import org.elasticsearch.search.aggregations.{AbstractAggregationBuilder, AggregationBuilder, AggregationBuilders}
import org.elasticsearch.search.aggregations.bucket.terms.{TermsBuilder, Terms}
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramBuilder, HistogramBuilder, DateHistogram}
import org.elasticsearch.common.geo.{GeoPoint, GeoDistance}
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceBuilder
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder


/** @author Nicolas Yzet */

trait AggregationDsl {
  def aggregation = new AggregationExpectingType
  def agg = aggregation

  class AggregationExpectingType {
    def terms(name: String) = new TermAggregationDefinition(name)
    def range(name: String) = new RangeAggregationDefinition(name)
    def histogram(name: String) = new HistogramAggregation(name)
    def datehistogram(name: String) = new DateHistogramAggregation(name)
    def filter(name: String) = new FilterAggregationDefinition(name)
    def geodistance(name: String) = new GeoDistanceAggregationDefinition(name)
  }
}

trait AbstractAggregationDefinition {
  def builder: AbstractAggregationBuilder
}

trait AggregationDefinition[+Self <: AggregationDefinition[Self, B], B <: AggregationBuilder[B]] extends AbstractAggregationDefinition {
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

class TermAggregationDefinition(name: String) extends AggregationDefinition[TermAggregationDefinition, TermsBuilder] {
  val aggregationBuilder = AggregationBuilders.terms(name)

  //def builder = builder

  def size(size: Int): TermAggregationDefinition = {
    builder.size(size)
    this
  }

  def lang(lang: String): TermAggregationDefinition = {
    builder.lang(lang)
    this
  }

  def order(order: Terms.Order): TermAggregationDefinition = {
    builder.order(order)
    this
  }

  def field(field: String): TermAggregationDefinition = {
    builder.field(field)
    this
  }

  def script(script: String): TermAggregationDefinition = {
    builder.script(script)
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

  def exclude(regex: String): TermAggregationDefinition = {
    builder.exclude(regex)
    this
  }
}

class RangeAggregationDefinition(name: String) extends AggregationDefinition[RangeAggregationDefinition, RangeBuilder] {
  val aggregationBuilder = AggregationBuilders.range(name)

  def range(from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(key, from, to)
    this
  }

  def field(field: String): RangeAggregationDefinition = {
    builder.field(field)
    this
  }
}

class DateRangeAggregation(name: String) extends AggregationDefinition[DateRangeAggregation, DateRangeBuilder] {
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

  def field(field: String): DateRangeAggregation = {
    builder.field(field)
    this
  }

  def format(fmt: String): DateRangeAggregation = {
    builder.format(fmt)
    this
  }
}


class HistogramAggregation(name: String) extends AggregationDefinition[HistogramAggregation, HistogramBuilder] {
  val aggregationBuilder = AggregationBuilders.histogram(name)
  
  def field(field: String): HistogramAggregation = {
    builder.field(field)
    this
  }

  def interval(interval: Long): HistogramAggregation = {
    builder.interval(interval)
    this
  }
}


class DateHistogramAggregation(name: String) extends AggregationDefinition[DateHistogramAggregation, DateHistogramBuilder] {
  val aggregationBuilder = AggregationBuilders.dateHistogram(name)


  def field(field: String): DateHistogramAggregation = {
    builder.field(field)
    this
  }

  def interval(interval: Long): DateHistogramAggregation = {
    builder.interval(interval)
    this
  }

  def interval(interval: DateHistogram.Interval):  DateHistogramAggregation = {
    builder.interval(interval)
    this
  }
}

class GeoDistanceAggregationDefinition(name: String) extends AggregationDefinition[GeoDistanceAggregationDefinition, GeoDistanceBuilder] {
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

class FilterAggregationDefinition(name: String) extends AggregationDefinition[FilterAggregationDefinition, FilterAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.filter(name)

  def filter(block: => FilterDefinition): FilterAggregationDefinition = {
    builder.filter(block.builder)
    this
  }
}

