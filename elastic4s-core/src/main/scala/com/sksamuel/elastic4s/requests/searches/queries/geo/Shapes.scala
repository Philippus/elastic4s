package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.GeoPoint

object Shapes {

  case class Polygon(points: Seq[GeoPoint], holes: Option[Seq[Seq[GeoPoint]]])

  case class Circle(point: GeoPoint, distance: (Double, DistanceUnit))
}
