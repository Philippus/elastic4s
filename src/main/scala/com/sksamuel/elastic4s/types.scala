package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class FieldType(val elastic: String)
object FieldType {
    case object StringType extends FieldType("string")
    case object GeoPointType extends FieldType("geo_point")
    case object IntegerType extends FieldType("integer")
    case object DateType extends FieldType("date")
    case object FloatType extends FieldType("float")
    case object BinaryType extends FieldType("binary")
    case object AttachmentType extends FieldType("attachment")
    case object IpType extends FieldType("ip")
    case object GeoShapeType extends FieldType("geo_shape")
}
