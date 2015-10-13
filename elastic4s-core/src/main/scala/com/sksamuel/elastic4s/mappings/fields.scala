package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings.attributes._
import org.elasticsearch.common.xcontent.XContentBuilder

import scala.language.implicitConversions

case class GetMappingDefinition(indexes: Iterable[String]) {
  var types: Iterable[String] = Nil
  def types(types: String*): this.type = {
    this.types = types
    this
  }
}

case class DeleteMappingDefinition(indexes: Iterable[String]) {
  var types: Iterable[String] = Nil
  def types(types: String*): this.type = {
    this.types = types
    this
  }
}

sealed abstract class DynamicMapping

object DynamicMapping {
  case object Strict extends DynamicMapping
  case object Dynamic extends DynamicMapping
  case object False extends DynamicMapping
}

trait TypeableFields {
  val name: String
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: AttachmentType.type) = new AttachmentFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: BinaryType.type) = new BinaryFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: BooleanType.type) = new BooleanFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: ByteType.type) = new ByteFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: CompletionType.type) = new CompletionFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: DateType.type) = new DateFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: DoubleType.type) = new DoubleFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: FloatType.type) = new FloatFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: GeoPointType.type) = new GeoPointFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: GeoShapeType.type) = new GeoShapeFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: IntegerType.type) = new IntegerFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: IpType.type) = new IpFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: LongType.type) = new LongFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: MultiFieldType.type) = new MultiFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: NestedType.type): NestedFieldDefinition = new NestedFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: ObjectType.type): ObjectFieldDefinition = new ObjectFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: ShortType.type) = new ShortFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: StringType.type) = new StringFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def withType(ft: TokenCountType.type) = new TokenCountDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: AttachmentType.type) = new AttachmentFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: BinaryType.type) = new BinaryFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: BooleanType.type) = new BooleanFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: ByteType.type) = new ByteFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: CompletionType.type) = new CompletionFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: DateType.type) = new DateFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: DoubleType.type) = new DoubleFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: FloatType.type) = new FloatFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: GeoPointType.type) = new GeoPointFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: GeoShapeType.type) = new GeoShapeFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: IntegerType.type) = new IntegerFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: IpType.type) = new IpFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: LongType.type) = new LongFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: MultiFieldType.type) = new MultiFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: NestedType.type): NestedFieldDefinition = new NestedFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: ObjectType.type): ObjectFieldDefinition = new ObjectFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: ShortType.type) = new ShortFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: StringType.type) = new StringFieldDefinition(name)
  @deprecated("use field(name, type)", "2.0.0")
  def typed(ft: TokenCountType.type) = new TokenCountDefinition(name)

  def nested(fields: TypedFieldDefinition*) = new NestedFieldDefinition(name).as(fields: _*)
  def inner(fields: TypedFieldDefinition*) = new ObjectFieldDefinition(name).as(fields: _*)
  def multi(fields: TypedFieldDefinition*) = new MultiFieldDefinition(name).as(fields: _*)
}

case class FieldDefinition(name: String) extends AttributeAnalyzer with TypeableFields

abstract class TypedFieldDefinition(val `type`: FieldType, name: String) extends FieldDefinition(name) {

  protected def insertType(source: XContentBuilder): Unit = {
    source.field("type", `type`.elastic)
  }

  private[elastic4s] def build(source: XContentBuilder, startObject: Boolean = true): Unit
}

/** @author Fehmi Can Saglam */
final class NestedFieldDefinition(name: String)
  extends TypedFieldDefinition(NestedType, name) {

  var _fields: Seq[TypedFieldDefinition] = Nil

  def as(fields: TypedFieldDefinition*): NestedFieldDefinition = {
    _fields = fields
    this
  }

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    source.startObject("properties")
    for ( field <- _fields ) {
      field.build(source)
    }
    source.endObject()

    if (startObject)
      source.endObject()
  }
}

/** @author Fehmi Can Saglam */
final class ObjectFieldDefinition(name: String)
  extends TypedFieldDefinition(ObjectType, name)
  with AttributeEnabled {

  var _fields: Seq[TypedFieldDefinition] = Nil

  def as(fields: TypedFieldDefinition*): ObjectFieldDefinition = {
    _fields = fields
    this
  }

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeEnabled].insert(source)
    if (_fields.nonEmpty) {
      source.startObject("properties")
      for ( field <- _fields ) {
        field.build(source)
      }
      source.endObject()
    }

    if (startObject)
      source.endObject()
  }
}

final class StringFieldDefinition(name: String)
  extends TypedFieldDefinition(StringType, name)
  with AttributeIndexName
  with AttributeStore
  with AttributeIndex
  with AttributeTermVector
  with AttributeBoost
  with AttributeNullValue[String]
  with AttributeOmitNorms
  with AttributeAnalyzer
  with AttributeIndexOptions
  with AttributeSearchAnalyzer
  with AttributeIncludeInAll
  with AttributeIgnoreAbove
  with AttributePositionOffsetGap
  with AttributePostingsFormat
  with AttributeDocValues
  with AttributeSimilarity
  with AttributeCopyTo
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeAnalyzer].insert(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValues].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeIndexOptions].insert(source)
    super[AttributeIgnoreAbove].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributeOmitNorms].insert(source)
    super[AttributePositionOffsetGap].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributeSearchAnalyzer].insert(source)
    super[AttributeSimilarity].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeTermVector].insert(source)
    super[AttributeCopyTo].insert(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

abstract class NumberFieldDefinition[T](`type`: FieldType, name: String)
  extends TypedFieldDefinition(`type`, name)
  with AttributeBoost
  with AttributeIncludeInAll
  with AttributeIgnoreMalformed
  with AttributeIndex
  with AttributeIndexName
  with AttributeNullValue[T]
  with AttributePostingsFormat
  with AttributePrecisionStep
  with AttributeStore
  with AttributeDocValues
  with AttributeCopyTo
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValues].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeIgnoreMalformed].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributePrecisionStep].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class FloatFieldDefinition(name: String) extends NumberFieldDefinition[Float](FloatType, name)
final class DoubleFieldDefinition(name: String) extends NumberFieldDefinition[Double](DoubleType, name)
final class ByteFieldDefinition(name: String) extends NumberFieldDefinition[Byte](ByteType, name)
final class ShortFieldDefinition(name: String) extends NumberFieldDefinition[Short](ShortType, name)
final class IntegerFieldDefinition(name: String) extends NumberFieldDefinition[Int](IntegerType, name)
final class LongFieldDefinition(name: String) extends NumberFieldDefinition[Long](LongType, name)

final class DateFieldDefinition(name: String)
  extends TypedFieldDefinition(DateType, name)
  with AttributeBoost
  with AttributeFormat
  with AttributeIncludeInAll
  with AttributeIndex
  with AttributeIndexName
  with AttributeIgnoreMalformed
  with AttributeNullValue[String]
  with AttributePostingsFormat
  with AttributePrecisionStep
  with AttributeStore
  with AttributeDocValues
  with AttributeCopyTo
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValues].insert(source)
    super[AttributeFormat].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeIgnoreMalformed].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributePrecisionStep].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class BooleanFieldDefinition(name: String)
  extends TypedFieldDefinition(BooleanType, name)
  with AttributeIndexName
  with AttributeStore
  with AttributeIndex
  with AttributeBoost
  with AttributeNullValue[Boolean]
  with AttributeIncludeInAll
  with AttributePostingsFormat
  with AttributeDocValues
  with AttributeCopyTo
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValues].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class BinaryFieldDefinition(name: String)
  extends TypedFieldDefinition(BinaryType, name)
  with AttributeIndexName
  with AttributePostingsFormat
  with AttributeDocValues {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeDocValues].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributePostingsFormat].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class GeoPointFieldDefinition(name: String)
  extends TypedFieldDefinition(GeoPointType, name)
  with AttributeLatLon
  with AttributeGeohash
  with AttributeGeohashPrecision
  with AttributeGeohashPrefix
  with AttributeStore
  with AttributeValidate
  with AttributeValidateLat
  with AttributeValidateLon
  with AttributeNormalize
  with AttributeNormalizeLat
  with AttributeNormalizeLon {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeGeohash].insert(source)
    super[AttributeGeohashPrecision].insert(source)
    super[AttributeGeohashPrefix].insert(source)
    super[AttributeLatLon].insert(source)
    super[AttributeNormalize].insert(source)
    super[AttributeNormalizeLat].insert(source)
    super[AttributeNormalizeLon].insert(source)
    super[AttributeValidate].insert(source)
    super[AttributeValidateLat].insert(source)
    super[AttributeValidateLon].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class GeoShapeFieldDefinition(name: String)
  extends TypedFieldDefinition(GeoShapeType, name)
  with AttributeStore
  with AttributeTree
  with AttributePrecision {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributePrecision].insert(source)
    super[AttributeTree].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class IpFieldDefinition(name: String)
  extends TypedFieldDefinition(IpType, name)
  with AttributeIndexName
  with AttributeStore
  with AttributeIndex
  with AttributePrecisionStep
  with AttributeBoost
  with AttributeNullValue[String]
  with AttributeIncludeInAll
  with AttributeCopyTo
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePrecisionStep].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class AttachmentFieldDefinition(name: String)
  extends TypedFieldDefinition(AttachmentType, name)
  with AttributeFields {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeFields].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class CompletionFieldDefinition(name: String)
  extends TypedFieldDefinition(CompletionType, name)
  with AttributeAnalyzer
  with AttributeSearchAnalyzer
  with AttributePayloads
  with AttributePreserveSeparators
  with AttributePreservePositionIncrements
  with AttributeMaxInputLen {

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeAnalyzer].insert(source)
    super[AttributeSearchAnalyzer].insert(source)
    super[AttributePayloads].insert(source)
    super[AttributePreserveSeparators].insert(source)
    super[AttributePreservePositionIncrements].insert(source)
    super[AttributeMaxInputLen].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class TokenCountDefinition(name: String) extends TypedFieldDefinition(TokenCountType, name)
with AttributeIndex
with AttributeAnalyzer
with AttributeSearchAnalyzer {
  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributeAnalyzer].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeSearchAnalyzer].insert(source)

    if (startObject)
      source.endObject()
  }
}

final class MultiFieldDefinition(name: String)
  extends TypedFieldDefinition(MultiFieldType, name)
  with AttributePath {

  var _fields: Seq[TypedFieldDefinition] = Nil

  def as(fields: TypedFieldDefinition*) = {
    _fields = fields
    this
  }

  def build(source: XContentBuilder, startObject: Boolean = true): Unit = {
    if (startObject)
      source.startObject(name)

    insertType(source)
    super[AttributePath].insert(source)
    if (_fields.nonEmpty) {
      source.startObject("fields")
      for ( field <- _fields ) {
        field.build(source)
      }
      source.endObject()
    }

    if (startObject)
      source.endObject()
  }
}

case class RoutingDefinition(required: Boolean,
                             path: Option[String])

