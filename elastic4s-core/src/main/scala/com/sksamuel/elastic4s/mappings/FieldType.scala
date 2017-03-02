package com.sksamuel.elastic4s.mappings

abstract class FieldType(val elastic: String)

object FieldType {
  case object AttachmentType extends FieldType("attachment")
  case object BinaryType extends FieldType("binary")
  case object BooleanType extends FieldType("boolean")
  case object ByteType extends FieldType("byte")
  case object CompletionType extends FieldType("completion")
  case object DateType extends FieldType("date")
  case object DoubleType extends FieldType("double")
  case object FloatType extends FieldType("float")
  case object IntegerType extends FieldType("integer")
  case object IpType extends FieldType("ip")
  case object GeoPointType extends FieldType("geo_point")
  case object GeoHashType extends FieldType("geo_hash")
  case object GeoShapeType extends FieldType("geo_shape")
  case object KeywordType extends FieldType("keyword")
  case object LongType extends FieldType("long")
  case object NestedType extends FieldType("nested")
  case object ObjectType extends FieldType("object")
  case object PercolatorType extends FieldType("percolator")
  case object ShortType extends FieldType("short")

  @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
  case object StringType extends FieldType("string")

  case object TextType extends FieldType("text")
  case object TokenCountType extends FieldType("token_count")
}
