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

  val COERCE: GeoValidationMethod             = Coerce
  val IGNORE_MALFORMED: GeoValidationMethod   = IgnoreMalformed
  val STRICT: GeoValidationMethod             = Strict
}
