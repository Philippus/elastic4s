package com.sksamuel.elastic4s.mapping

import scala.collection.mutable.ListBuffer
import org.elasticsearch.common.xcontent.{ XContentFactory, XContentBuilder }
import com.sksamuel.elastic4s.mapping.attributes._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mapping.FieldType._

/** @author Stephen Samuel */
trait MappingDsl {
  def id: FieldDefinition = "_id"
  implicit def field(name: String): FieldDefinition = new FieldDefinition(name)
  implicit def map(`type`: String) = new MappingDefinition(`type`)
}

class MappingDefinition(val `type`: String) {

  var _all = true
  var _source = true
  var date_detection = false
  var numeric_detection = true
  var _size = false
  var dynamic_date_formats: Iterable[String] = Nil
  val _fields = new ListBuffer[TypedFieldDefinition]
  var _analyzer: Option[String] = None
  var _boostName: Option[String] = None
  var _boostValue: Double = 0
  var _dynamic: DynamicMapping = Dynamic
  var _meta: Map[String, Any] = Map.empty
  var _routing: Option[RoutingDefinition] = None

  def all(enabled: Boolean): MappingDefinition = {
    _all = enabled
    this
  }
  def analyzer(analyzer: String): MappingDefinition = {
    _analyzer = Option(analyzer)
    this
  }
  def analyzer(analyzer: Analyzer): MappingDefinition = {
    _analyzer = Option(analyzer.name)
    this
  }
  def boost(name: String): MappingDefinition = {
    _boostName = Option(name)
    this
  }
  def boostNullValue(value: Double): MappingDefinition = {
    _boostValue = value
    this
  }
  def dynamic(dynamic: DynamicMapping): MappingDefinition = {
    _dynamic = dynamic
    this
  }
  def dynamic(dynamic: Boolean): MappingDefinition = {
    _dynamic = dynamic match {
      case true => Dynamic
      case false => False
    }
    this
  }

  def dynamicDateFormats(dynamic_date_formats: String*): MappingDefinition = {
    this.dynamic_date_formats = dynamic_date_formats
    this
  }
  def meta(map: Map[String, Any]): MappingDefinition = {
    this._meta = map
    this
  }
  def routing(required: Boolean, path: Option[String] = None): MappingDefinition = {
    this._routing = Some(RoutingDefinition(required, path))
    this
  }
  def source(source: Boolean): MappingDefinition = {
    this._source = source
    this
  }
  def dateDetection(date_detection: Boolean): MappingDefinition = {
    this.date_detection = date_detection
    this
  }
  def numericDetection(numeric_detection: Boolean): MappingDefinition = {
    this.numeric_detection = numeric_detection
    this
  }
  def as(iterable: Iterable[TypedFieldDefinition]): MappingDefinition = {
    _fields ++= iterable
    this
  }
  def as(fields: TypedFieldDefinition*): MappingDefinition = as(fields.toIterable)
  def size(size: Boolean): MappingDefinition = {
    _size = size
    this
  }

  def build: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    build(builder)
    builder.endObject()
  }

  def build(source: XContentBuilder): Unit = {
    source.startObject(`type`)

    source.startObject("_all").field("enabled", _all).endObject()
    source.startObject("_source").field("enabled", _source).endObject()
    if (dynamic_date_formats.size > 0)
      source.field("dynamic_date_formats", dynamic_date_formats.toArray: _*)
    if (date_detection) source.field("date_detection", date_detection)
    if (numeric_detection) source.field("numeric_detection", numeric_detection)
    source.field("dynamic", _dynamic match {
      case Strict => "strict"
      case False => "false"
      case _ => "dynamic"
    })

    _boostName.foreach(arg =>
      source.startObject("_boost").field("name", arg).field("null_value", _boostValue).endObject()
    )

    _analyzer.foreach(arg => source.startObject("_analyzer").field("path", arg).endObject())
    if (_size) source.startObject("_size").field("enabled", true).endObject()

    source.startObject("properties")
    for (field <- _fields) {
      field.build(source)
    }
    source.endObject() // end properties

    if (_meta.size > 0) {
      source.startObject("_meta")
      for (meta <- _meta) {
        source.field(meta._1, meta._2)
      }
      source.endObject()
    }

    if (_routing.isDefined) {
      source.startObject("_routing").field("required", _routing.get.required)
      if (_routing.get.path.isDefined) {
        source.field("path", _routing.get.path.get)
      }
      source.endObject()
    }

    source.endObject() // end mapping name
  }
}

sealed abstract class DynamicMapping
case object Strict extends DynamicMapping
case object Dynamic extends DynamicMapping
case object False extends DynamicMapping

private[mapping] class FieldDefinition(val name: String) {

  def typed(ft: AttachmentType.type) = new AttachmentFieldDefinition(name)
  def typed(ft: BinaryType.type) = new BinaryFieldDefinition(name)
  def typed(ft: BooleanType.type) = new BooleanFieldDefinition(name)
  def typed(ft: ByteType.type) = new ByteFieldDefinition(name)
  def typed(ft: CompletionType.type) = new CompletionFieldDefinition(name)
  def typed(ft: DateType.type) = new DateFieldDefinition(name)
  def typed(ft: DoubleType.type) = new DoubleFieldDefinition(name)
  def typed(ft: FloatType.type) = new FloatFieldDefinition(name)
  def typed(ft: GeoPointType.type) = new GeoPointFieldDefinition(name)
  def typed(ft: GeoShapeType.type) = new GeoShapeFieldDefinition(name)
  def typed(ft: IntegerType.type) = new IntegerFieldDefinition(name)
  def typed(ft: IpType.type) = new IpFieldDefinition(name)
  def typed(ft: LongType.type) = new LongFieldDefinition(name)
  def typed(ft: MultiFieldType.type) = new MultiFieldDefinition(name)
  def typed(ft: NestedType.type): NestedFieldDefinition = new NestedFieldDefinition(name)
  def typed(ft: ObjectType.type): ObjectFieldDefinition = new ObjectFieldDefinition(name)
  def typed(ft: ShortType.type) = new ShortFieldDefinition(name)
  def typed(ft: StringType.type) = new StringFieldDefinition(name)
  def typed(ft: TokenCountType.type) = new TokenCountDefinition(name)

  def nested(fields: TypedFieldDefinition*) = new NestedFieldDefinition(name).as(fields: _*)
  def inner(fields: TypedFieldDefinition*) = new ObjectFieldDefinition(name).as(fields: _*)
  def multi(fields: StringFieldDefinition*) = new MultiFieldDefinition(name).as(fields: _*)
}

abstract class TypedFieldDefinition(val `type`: FieldType, name: String) extends FieldDefinition(name) {

  protected def insertType(source: XContentBuilder): Unit = {
    source.field("type", `type`.elastic)
  }

  private[mapping] def build(source: XContentBuilder): Unit
}

/** @author Fehmi Can Saglam */
final class NestedFieldDefinition(name: String)
    extends TypedFieldDefinition(NestedType, name) {

  var _fields: Seq[TypedFieldDefinition] = Nil

  def as(fields: TypedFieldDefinition*): NestedFieldDefinition = {
    _fields = fields
    this
  }

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    source.startObject("properties")
    for (field <- _fields) {
      field.build(source)
    }
    source.endObject()
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

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeEnabled].insert(source)
    if (!_fields.isEmpty) {
      source.startObject("properties")
      for (field <- _fields) {
        field.build(source)
      }
      source.endObject()
    }
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
    with AttributeIndexOptions
    with AttributeAnalyzer
    with AttributeIndexAnalyzer
    with AttributeSearchAnalyzer
    with AttributeIncludeInAll
    with AttributeIgnoreAbove
    with AttributePositionOffsetGap
    with AttributePostingsFormat
    with AttributeDocValuesFormat
    with AttributeSimilarity
    with AttributeCopyTo {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeAnalyzer].insert(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValuesFormat].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexAnalyzer].insert(source)
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
    with AttributeDocValuesFormat
    with AttributeCopyTo {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValuesFormat].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeIgnoreMalformed].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributePrecisionStep].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
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
    with AttributeDocValuesFormat
    with AttributeCopyTo {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValuesFormat].insert(source)
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
    with AttributeDocValuesFormat
    with AttributeCopyTo {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeBoost].insert(source)
    super[AttributeDocValuesFormat].insert(source)
    super[AttributeIncludeInAll].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributeNullValue].insert(source)
    super[AttributePostingsFormat].insert(source)
    super[AttributeStore].insert(source)
    super[AttributeCopyTo].insert(source)
    source.endObject()
  }
}

final class BinaryFieldDefinition(name: String)
    extends TypedFieldDefinition(BinaryType, name)
    with AttributeIndexName
    with AttributePostingsFormat
    with AttributeDocValuesFormat {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeDocValuesFormat].insert(source)
    super[AttributeIndexName].insert(source)
    super[AttributePostingsFormat].insert(source)
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

  def build(source: XContentBuilder): Unit = {
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
    source.endObject()
  }
}

final class GeoShapeFieldDefinition(name: String)
    extends TypedFieldDefinition(GeoShapeType, name)
    with AttributeStore
    with AttributeTree
    with AttributePrecision {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributePrecision].insert(source)
    super[AttributeTree].insert(source)
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
    with AttributeCopyTo {

  def build(source: XContentBuilder): Unit = {
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
    source.endObject()
  }
}

final class AttachmentFieldDefinition(name: String)
    extends TypedFieldDefinition(AttachmentType, name) {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    source.endObject()
  }
}

final class CompletionFieldDefinition(name: String)
    extends TypedFieldDefinition(CompletionType, name)
    with AttributeIndexAnalyzer
    with AttributeSearchAnalyzer
    with AttributePayloads
    with AttributePreserveSeparators
    with AttributePreservePositionIncrements
    with AttributeMaxInputLen {

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeIndexAnalyzer].insert(source)
    super[AttributeSearchAnalyzer].insert(source)
    super[AttributePayloads].insert(source)
    super[AttributePreserveSeparators].insert(source)
    super[AttributePreservePositionIncrements].insert(source)
    super[AttributeMaxInputLen].insert(source)
    source.endObject()
  }
}

final class TokenCountDefinition(name: String) extends TypedFieldDefinition(TokenCountType, name)
    with AttributeIndex
    with AttributeAnalyzer
    with AttributeIndexAnalyzer {
  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributeAnalyzer].insert(source)
    super[AttributeIndex].insert(source)
    super[AttributeIndexAnalyzer].insert(source)
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

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    insertType(source)
    super[AttributePath].insert(source)
    if (!_fields.isEmpty) {
      source.startObject("fields")
      for (field <- _fields) {
        field.build(source)
      }
      source.endObject()
    }
    source.endObject()
  }
}

case class RoutingDefinition(
  required: Boolean,
  path: Option[String])
