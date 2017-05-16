package com.sksamuel.elastic4s.searches.queries.geo

sealed trait GeoValidationMethod
object GeoValidationMethod {

  def valueOf(str: String): GeoValidationMethod = str.toUpperCase match {
    case "COERCE" => COERCE
    case "IGNORE_MALFORMED" => IGNORE_MALFORMED
    case "STRICT" => STRICT
  }

  case object COERCE extends GeoValidationMethod
  case object IGNORE_MALFORMED extends GeoValidationMethod
  case object STRICT extends GeoValidationMethod
}



