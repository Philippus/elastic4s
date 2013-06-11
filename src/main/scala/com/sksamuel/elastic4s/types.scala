package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait FieldType
object FieldType {
    case object StringType extends FieldType
    case object GeoPointType extends FieldType
    case object IntegerType extends FieldType
}
