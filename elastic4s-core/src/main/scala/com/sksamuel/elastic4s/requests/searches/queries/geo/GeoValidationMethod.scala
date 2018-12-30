package com.sksamuel.elastic4s.requests.searches.queries.geo

sealed trait GeoValidationMethod
object GeoValidationMethod {

  def valueOf(str: String): GeoValidationMethod = str.toUpperCase match {
    case "COERCE"                               => Coerce
    case "IGNORE_MALFORMED" | "IGNOREMALFORMED" => IgnoreMalformed
    case "STRICT"                               => Strict
  }

  case object Coerce          extends GeoValidationMethod
  case object IgnoreMalformed extends GeoValidationMethod
  case object Strict          extends GeoValidationMethod

  def COERCE           = Coerce
  def IGNORE_MALFORMED = IgnoreMalformed
  def STRICT           = Strict
}
