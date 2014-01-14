package com.sksamuel.elastic4s

import org.elasticsearch.search.aggregations.{AggregationBuilder, AggregationBuilders}
import org.elasticsearch.search.aggregations.bucket.terms.{TermsBuilder, Terms}
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import org.elasticsearch.common.geo.{GeoPoint, GeoDistance}


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


trait AggregationDefinition {
  def builder[B <: AggregationBuilder[B]]: AggregationBuilder[B]
}

class TermAggregationDefinition(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.terms(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def size(size: Int): TermAggregationDefinition = {
    _builder.size(size)
    this
  }

  def lang(lang: String): TermAggregationDefinition = {
    _builder.lang(lang)
    this
  }

  def order(order: Terms.Order): TermAggregationDefinition = {
    _builder.order(order)
    this
  }

  def field(field: String): TermAggregationDefinition = {
    _builder.field(field)
    this
  }

  def script(script: String): TermAggregationDefinition = {
    _builder.script(script)
    this
  }

  def shardSize(shardSize: Int): TermAggregationDefinition = {
    _builder.shardSize(shardSize)
    this
  }

  def include(regex: String): TermAggregationDefinition = {
    _builder.include(regex)
    this
  }

  def exclude(regex: String): TermAggregationDefinition = {
    _builder.exclude(regex)
    this
  }
}

class RangeAggregationDefinition(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.range(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def range(from: Double, to: Double): RangeAggregationDefinition = {
    _builder.addRange(from, to)
    this
  }

  def range(key: String, from: Double, to: Double): RangeAggregationDefinition = {
    _builder.addRange(key, from, to)
    this
  }

  def field(field: String): RangeAggregationDefinition = {
    _builder.field(field)
    this
  }
}

class DateRangeAggregation(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.dateRange(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def range(from: String, to: String): DateRangeAggregation = {
    _builder.addRange(from, to)
    this
  }

  def range(key: String, from: String, to: String): DateRangeAggregation = {
    _builder.addRange(key, from, to)
    this
  }

  def range(from: Long, to: Long): DateRangeAggregation = {
    _builder.addRange(from, to)
    this
  }

  def range(key: String, from: Long, to: Long): DateRangeAggregation = {
    _builder.addRange(key, from, to)
    this
  }

  def field(field: String): DateRangeAggregation = {
    _builder.field(field)
    this
  }

  def format(fmt: String): DateRangeAggregation = {
    _builder.format(fmt)
    this
  }
}


class HistogramAggregation(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.histogram(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]
  def field(field: String): HistogramAggregation = {
    _builder.field(field)
    this
  }

  def interval(interval: Long): HistogramAggregation = {
    _builder.interval(interval)
    this
  }
}


class DateHistogramAggregation(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.dateHistogram(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def field(field: String): DateHistogramAggregation = {
    _builder.field(field)
    this
  }

  def interval(interval: Long): DateHistogramAggregation = {
    _builder.interval(interval)
    this
  }

  def interval(interval: DateHistogram.Interval):  DateHistogramAggregation = {
    _builder.interval(interval)
    this
  }
}

class GeoDistanceAggregationDefinition(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.geoDistance(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def range(tuple: (Double, Double)): GeoDistanceAggregationDefinition = range(tuple._1, tuple._2)
  def range(from: Double, to: Double): GeoDistanceAggregationDefinition = {
    _builder.addRange(from, to)
    this
  }

  def field(field: String): GeoDistanceAggregationDefinition = {
    _builder.field(field)
    this
  }

  def geoDistance(geoDistance: GeoDistance): GeoDistanceAggregationDefinition = {
    _builder.distanceType(geoDistance)
    this
  }
  def geohash(geohash: String): GeoDistanceAggregationDefinition = {
    _builder.geohash(geohash)
    this
  }

  def point(lat: Double, long: Double): GeoDistanceAggregationDefinition = {
    _builder.point(new GeoPoint(lat, long))
    this
  }
  def addUnboundedFrom(addUnboundedFrom: Double): GeoDistanceAggregationDefinition = {
    _builder.addUnboundedFrom(addUnboundedFrom)
    this
  }
  def addUnboundedTo(addUnboundedTo: Double): GeoDistanceAggregationDefinition = {
    _builder.addUnboundedTo(addUnboundedTo)
    this
  }
}

class FilterAggregationDefinition(name: String) extends AggregationDefinition {
  val _builder = AggregationBuilders.filter(name)
  def builder[B <: AggregationBuilder[B]] = _builder.asInstanceOf[B]

  def filter(block: => FilterDefinition): FilterAggregationDefinition = {
    _builder.filter(block.builder)
    this
  }
}

