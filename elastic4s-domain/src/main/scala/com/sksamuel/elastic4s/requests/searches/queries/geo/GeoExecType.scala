package com.sksamuel.elastic4s.requests.searches.queries.geo

sealed trait GeoExecType
object GeoExecType {

  def valueOf(str: String): GeoExecType = str.toUpperCase match {
    case "MEMORY"  => Memory
    case "INDEXED" => Indexed
  }

  case object Memory  extends GeoExecType
  case object Indexed extends GeoExecType
}
