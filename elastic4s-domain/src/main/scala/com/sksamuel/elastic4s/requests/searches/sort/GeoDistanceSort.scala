package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.geo.{GeoDistance, GeoValidationMethod}
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class GeoDistanceSort(
    field: String,
    geohashes: Seq[String] = Nil,
    points: Seq[GeoPoint] = Nil,
    sortMode: Option[SortMode] = None,
    order: Option[SortOrder] = None,
    unit: Option[DistanceUnit] = None,
    validation: Option[GeoValidationMethod] = None,
    geoDistance: Option[GeoDistance] = None,
    ignoreUnmapped: Option[Boolean] = None,
    nested: Option[NestedSort] = None
) extends Sort {

  def mode(mode: String): GeoDistanceSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): GeoDistanceSort = copy(sortMode = mode.some)

  def sortMode(mode: String): GeoDistanceSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): GeoDistanceSort = copy(sortMode = mode.some)

  def nested(nested: NestedSort): GeoDistanceSort = copy(nested = nested.some)

  def order(order: SortOrder): GeoDistanceSort     = copy(order = order.some)
  def sortOrder(order: SortOrder): GeoDistanceSort = copy(order = order.some)

  def validation(validation: GeoValidationMethod): GeoDistanceSort = copy(validation = validation.some)

  def unit(unit: DistanceUnit): GeoDistanceSort           = copy(unit = unit.some)
  def geoDistance(distance: GeoDistance): GeoDistanceSort = copy(geoDistance = distance.some)

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoDistanceSort = copy(ignoreUnmapped = ignoreUnmapped.some)
}
