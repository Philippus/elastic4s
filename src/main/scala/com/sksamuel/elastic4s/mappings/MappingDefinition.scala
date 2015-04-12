package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.Analyzer
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

import scala.collection.mutable.ListBuffer

class MappingDefinition(val `type`: String) {

  var _all = true
  var _source = true
  var date_detection: Option[Boolean] = None
  var numeric_detection: Option[Boolean] = None
  var _size = false
  var dynamic_date_formats: Iterable[String] = Nil
  val _fields = new ListBuffer[TypedFieldDefinition]
  var _analyzer: Option[String] = None
  var _boostName: Option[String] = None
  var _boostValue: Double = 0
  var _parent: Option[String] = None
  var _dynamic: DynamicMapping = Dynamic
  var _meta: Map[String, Any] = Map.empty
  var _routing: Option[RoutingDefinition] = None
  var _timestamp: Option[TimestampDefinition] = None
  var _ttl = false
  var _useTtl = true
  val templates = new ListBuffer[DynamicTemplateDefinition]

  def useTtl(useTtl: Boolean): this.type = {
    _useTtl = useTtl
    this
  }

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

  def parent(parent: String): MappingDefinition = {
    _parent = Some(parent)
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

  def timestamp(enabled: Boolean,
                path: Option[String] = None,
                format: Option[String] = None,
                default: Option[String] = None) = {
    this._timestamp = Some(TimestampDefinition(enabled, path, format, default))
    this
  }

  def ttl(enabled: Boolean): MappingDefinition = {
    _ttl = enabled
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
    this.date_detection = Some(date_detection)
    this
  }
  def numericDetection(numeric_detection: Boolean): MappingDefinition = {
    this.numeric_detection = Some(numeric_detection)
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
    source.startObject("_all").field("enabled", _all).endObject()
    source.startObject("_source").field("enabled", _source).endObject()
    if (dynamic_date_formats.size > 0)
      source.field("dynamic_date_formats", dynamic_date_formats.toArray: _*)

    for ( dd <- date_detection ) source.field("date_detection", dd)
    for ( nd <- numeric_detection ) source.field("numeric_detection", nd)

    source.field("dynamic", _dynamic match {
      case Strict => "strict"
      case False => "false"
      case _ => "dynamic"
    })

    _boostName.foreach(arg =>
      source.startObject("_boost").field("name", arg).field("null_value", _boostValue).endObject()
    )

    _analyzer.foreach(arg => source.startObject("_analyzer").field("path", arg).endObject())

    _parent.foreach(arg => source.startObject("_parent").field("type", arg).endObject())

    if (_size) source.startObject("_size").field("enabled", true).endObject()

    _timestamp.foreach { timestamp =>
      source.startObject("_timestamp").field("enabled", timestamp.enabled)
      timestamp.path.foreach { path =>
        source.field("path", path)
      }
      timestamp.format.foreach { format =>
        source.field("format", format)
      }
      timestamp.default.foreach { default =>
        source.field("default", default)
      }
      source.endObject()
    }

    if (_useTtl)
      source.startObject("_ttl").field("enabled", _ttl).endObject()

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

    if (_routing.isDefined) {
      source.startObject("_routing").field("required", _routing.get.required)
      if (_routing.get.path.isDefined) {
        source.field("path", _routing.get.path.get)
      }
      source.endObject()
    }
  }
}
