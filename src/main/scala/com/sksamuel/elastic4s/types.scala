package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class FieldType(val elastic: String)
object FieldType {
  case object StringType extends FieldType("string")
  case object GeoPointType extends FieldType("geo_point")
  case object IntegerType extends FieldType("integer")
  case object BooleanType extends FieldType("boolean")
  case object DoubleType extends FieldType("double")
  case object LongType extends FieldType("long")
  case object ByteType extends FieldType("byte")
  case object DateType extends FieldType("date")
  case object FloatType extends FieldType("float")
  case object BinaryType extends FieldType("binary")
  case object AttachmentType extends FieldType("attachment")
  case object IpType extends FieldType("ip")
  case object GeoShapeType extends FieldType("geo_shape")
  case object NestedType extends FieldType("nested")
  case object ObjectType extends FieldType("object")
}
