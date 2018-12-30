package com.sksamuel.elastic4s.requests.searches.queries.geo

object GeoDistance {

  def valueOf(str: String): GeoDistance = str.toUpperCase match {
    case "PLANE" => Plane
    case "ARC"   => Arc
  }

  case object Arc   extends GeoDistance
  case object Plane extends GeoDistance

  @deprecated("use Arc", "6.0.0")
  def ARC = Arc

  @deprecated("use Plane", "6.0.0")
  def PLANE = Plane
}

sealed trait GeoDistance
