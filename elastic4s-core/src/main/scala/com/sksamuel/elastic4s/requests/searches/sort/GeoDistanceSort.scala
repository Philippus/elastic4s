package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.requests.searches.queries.geo.{GeoDistance, GeoValidationMethod}
import com.sksamuel.exts.OptionImplicits._

case class GeoDistanceSort(field: String,
                           geohashes: Seq[String] = Nil,
                           points: Seq[GeoPoint] = Nil,
                           nestedFilter: Option[Query] = None,
                           nestedPath: Option[String] = None,
                           sortMode: Option[SortMode] = None,
                           order: Option[SortOrder] = None,
                           unit: Option[DistanceUnit] = None,
                           validation: Option[GeoValidationMethod] = None,
                           geoDistance: Option[GeoDistance] = None)
    extends Sort {

  def mode(mode: String): GeoDistanceSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): GeoDistanceSort = copy(sortMode = mode.some)

  def sortMode(mode: String): GeoDistanceSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): GeoDistanceSort = copy(sortMode = mode.some)

  def nestedPath(path: String): GeoDistanceSort   = copy(nestedPath = path.some)
  def nestedFilter(query: Query): GeoDistanceSort = copy(nestedFilter = query.some)

  def order(order: SortOrder): GeoDistanceSort     = copy(order = order.some)
  def sortOrder(order: SortOrder): GeoDistanceSort = copy(order = order.some)

  def validation(validation: GeoValidationMethod): GeoDistanceSort = copy(validation = validation.some)

  def unit(unit: DistanceUnit): GeoDistanceSort           = copy(unit = unit.some)
  def geoDistance(distance: GeoDistance): GeoDistanceSort = copy(geoDistance = distance.some)
}
