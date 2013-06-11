package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait FieldType
object FieldType {
    case object String extends FieldType
    case object GeoPoint extends FieldType
    case object Integer extends FieldType
}
