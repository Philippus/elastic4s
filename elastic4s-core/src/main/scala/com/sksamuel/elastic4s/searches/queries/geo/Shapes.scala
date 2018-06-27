package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.DistanceUnit
import com.sksamuel.elastic4s.searches.GeoPoint

object Shapes {

  case class Polygon(points: Seq[GeoPoint], holes: Option[Seq[Seq[GeoPoint]]])

  case class Circle(point: GeoPoint, distance: (Double, DistanceUnit))
}
