package com.sksamuel.elastic4s

import scala.collection.mutable.ListBuffer
import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
trait MappingDsl {
  def id: FieldDefinition = field("_id")
  implicit def field(name: String): FieldDefinition = new FieldDefinition(name)
  implicit def map(`type`: String) = new MappingDefinition(`type`)
}

class MappingDefinition(val `type`: String) {

  var _source = true
  var date_detection = false
  var numeric_detection = true
  var _size = false
  var dynamic_date_formats: Iterable[String] = Nil
  val _fields = new ListBuffer[FieldDefinition]
  var _analyzer: Option[String] = None
  var _boostName: Option[String] = None
  var _boostValue: Double = 0
  var _meta: Map[String, Any] = Map.empty

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
  def dynamicDateFormats(dynamic_date_formats: String*): MappingDefinition = {
    this.dynamic_date_formats = dynamic_date_formats
    this
  }
  def meta(map: Map[String, Any]): MappingDefinition = {
    this._meta = map
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
  def as(iterable: Iterable[FieldDefinition]): MappingDefinition = {
    _fields ++= iterable
    this
  }
  def as(fields: FieldDefinition*): MappingDefinition = as(fields.toIterable)
  def size(size: Boolean): MappingDefinition = {
    _size = size
    this
  }

  def build(source: XContentBuilder): Unit = {
    source.startObject(`type`)

    source.startObject("_source").field("enabled", _source).endObject()
    if (dynamic_date_formats.size > 0)
      source.field("dynamic_date_formats", dynamic_date_formats.toArray: _*)
    if (date_detection) source.field("date_detection", date_detection)
    if (numeric_detection) source.field("numeric_detection", numeric_detection)

    _boostName.foreach(arg =>
      source.startObject("_boost").field("name", arg).field("null_value", _boostValue).endObject()
    )

    _analyzer.foreach(arg => source.startObject("_analyzer").field("path", arg).endObject())
    if (_size) source.startObject("_size").field("enabled", true).endObject()

    source.startObject("properties")
    for ( field <- _fields ) {
      field.build(source)
    }
    source.endObject() // end properties

    if (_meta.size > 0) {
      source.startObject("_meta")
      for ( meta <- _meta ) {
        source.field(meta._1, meta._2)
      }
      source.endObject()
    }

    source.endObject() // end mapping name
  }
}

class FieldDefinition(val name: String) {

  var _type: Option[FieldType] = None
  var _analyzer: Option[Analyzer] = None
  var _index: Option[String] = None
  var _store: Boolean = false
  var _boost: Double = 0
  var _nullValue: Option[String] = None
  var _omitNorms: Option[Boolean] = None
  var _position_offset_gap: Int = 0
  var _ignoreAbove: Option[Int] = None
  var _includeInAll: Option[Boolean] = None
  var _nested: List[FieldDefinition] = Nil

  def nested(fields: FieldDefinition*): FieldDefinition = {
    _nested = fields.toList
    this
  }

  def typed(ft: FieldType): FieldDefinition = fieldType(ft)
  def fieldType(ft: FieldType): FieldDefinition = {
    _type = Option(ft)
    this
  }

  def analyzer(a: Analyzer): FieldDefinition = {
    _analyzer = Option(a)
    this
  }

  def store(store: Boolean): FieldDefinition = {
    _store = store
    this
  }

  def ignoreAbove(ignoreAbove: Int): FieldDefinition = {
    _ignoreAbove = Option(ignoreAbove)
    this
  }

  def boost(boost: Double): FieldDefinition = {
    _boost = boost
    this
  }

  def omitNorms(omitNorms: Boolean): FieldDefinition = {
    _omitNorms = Option(omitNorms)
    this
  }

  def includeInAll(includeInAll: Boolean): FieldDefinition = {
    _includeInAll = Option(includeInAll)
    this
  }

  def nullValue(nullValue: String): FieldDefinition = {
    _nullValue = Option(nullValue)
    this
  }

  def index(index: String): FieldDefinition = {
    _index = Option(index)
    this
  }

  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    _type.foreach(arg => source.field("type", arg.elastic))
    _analyzer.foreach(arg => source.field("analyzer", arg.name))
    _index.foreach(index => source.field("index", index))
    _omitNorms.foreach(omitNorms => source.field("omit_norms", omitNorms))
    _nullValue.foreach(nullValue => source.field("null_value", nullValue))
    _includeInAll.foreach(includeInAll => source.field("include_in_all", includeInAll))
    if (_boost > 0) source.field("boost", _boost)
    if (_store) source.field("store", "yes")

    for ( field <- _nested ) {
      field.build(source)
    }

    source.endObject()
  }
}