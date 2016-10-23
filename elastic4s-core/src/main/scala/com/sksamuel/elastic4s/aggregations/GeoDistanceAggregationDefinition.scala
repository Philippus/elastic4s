package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceAggregationBuilder

case class GeoDistanceAggregationDefinition(name: String, origin: GeoPoint)
  extends AggregationDefinition[GeoDistanceAggregationDefinition, GeoDistanceAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.geoDistance(name, origin)

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

  def addUnboundedFrom(addUnboundedFrom: Double): GeoDistanceAggregationDefinition = {
    builder.addUnboundedFrom(addUnboundedFrom)
    this
  }

  def addUnboundedTo(addUnboundedTo: Double): GeoDistanceAggregationDefinition = {
    builder.addUnboundedTo(addUnboundedTo)
    this
  }
}
