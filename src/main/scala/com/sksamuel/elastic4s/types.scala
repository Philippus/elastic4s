package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class FieldType(val elastic: String)
object FieldType {
    case object StringType extends FieldType("string")
    case object GeoPointType extends FieldType("geopoint")
    case object IntegerType extends FieldType("integer")
}
