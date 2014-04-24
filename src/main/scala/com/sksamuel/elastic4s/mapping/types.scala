package com.sksamuel.elastic4s.mapping

/** @author Stephen Samuel */
abstract class FieldType(val elastic: String)
object FieldType {
  case object IntegerType extends FieldType("integer")
  case object BooleanType extends FieldType("boolean")
  case object AttachmentType extends FieldType("attachment")
  case object BinaryType extends FieldType("binary")
  case object ByteType extends FieldType("byte")
  case object CompletionType extends FieldType("completion")
  case object DateType extends FieldType("date")
  case object DoubleType extends FieldType("double")
  case object FloatType extends FieldType("float")
  case object IpType extends FieldType("ip")
  case object GeoPointType extends FieldType("geo_point")
  case object GeoShapeType extends FieldType("geo_shape")
  case object LongType extends FieldType("long")
  case object MultiFieldType extends FieldType("multi_field")
  case object NestedType extends FieldType("nested")
  case object ObjectType extends FieldType("object")
  case object ShortType extends FieldType("short")
  case object StringType extends FieldType("string")
  case object TokenCountType extends FieldType("token_count")
  case class ParentType(_parent: String) extends FieldType(_parent)
}

