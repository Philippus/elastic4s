package com.sksamuel.elastic4s.searches.queries.geo

object GeoDistance {

  def valueOf(str: String): GeoDistance = str.toUpperCase match {
    case "PLANE" => Plane
    case "ARC" => Arc
  }

  case object Arc extends GeoDistance
  case object Plane extends GeoDistance
}

sealed trait GeoDistance
