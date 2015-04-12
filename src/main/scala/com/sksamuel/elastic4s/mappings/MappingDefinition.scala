package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.Analyzer
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

import scala.collection.mutable.ListBuffer

class MappingDefinition(val `type`: String) {

  var _all: Option[Boolean] = None
  var _source: Option[Boolean] = None
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
  var _templates: Iterable[DynamicTemplateDefinition] = Nil

  def useTtl(useTtl: Boolean): this.type = {
    _useTtl = useTtl
    this
  }

  def all(enabled: Boolean): this.type = {
    _all = Option(enabled)
    this
  }
  def analyzer(analyzer: String): this.type = {
    _analyzer = Option(analyzer)
    this
  }
  def analyzer(analyzer: Analyzer): this.type = {
    _analyzer = Option(analyzer.name)
    this
  }
  def boost(name: String): this.type = {
    _boostName = Option(name)
    this
  }
  def boostNullValue(value: Double): this.type = {
    _boostValue = value
    this
  }

  def parent(parent: String): this.type = {
    _parent = Some(parent)
    this
  }

  def dynamic(dynamic: DynamicMapping): this.type = {
    _dynamic = dynamic
    this
  }
  def dynamic(dynamic: Boolean): this.type = {
    _dynamic = dynamic match {
      case true => Dynamic
      case false => False
    }
    this
  }

  def timestamp(enabled: Boolean,
                path: Option[String] = None,
                format: Option[String] = None,
                default: Option[String] = None): this.type = {
    this._timestamp = Some(TimestampDefinition(enabled, path, format, default))
    this
  }

  def ttl(enabled: Boolean): this.type = {
    _ttl = enabled
    this
  }

  def dynamicDateFormats(dynamic_date_formats: String*): this.type = {
    this.dynamic_date_formats = dynamic_date_formats
    this
  }
  def meta(map: Map[String, Any]): this.type = {
    this._meta = map
    this
  }
  def routing(required: Boolean, path: Option[String] = None): this.type = {
    this._routing = Some(RoutingDefinition(required, path))
    this
  }
  def source(source: Boolean): this.type = {
    this._source = Option(source)
    this
  }
  def dateDetection(date_detection: Boolean): this.type = {
    this.date_detection = Some(date_detection)
    this
  }
  def numericDetection(numeric_detection: Boolean): this.type = {
    this.numeric_detection = Some(numeric_detection)
    this
  }
  def as(iterable: Iterable[TypedFieldDefinition]): this.type = {
    _fields ++= iterable
    this
  }
  def as(fields: TypedFieldDefinition*): this.type = as(fields.toIterable)
  def size(size: Boolean): MappingDefinition = {
    _size = size
    this
  }

  def templates(temps: Iterable[DynamicTemplateDefinition]): this.type = templates(temps)
  def templates(temps: DynamicTemplateDefinition*): this.type = {
    _templates = temps
    this
  }

  def build: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    build(builder)
    builder.endObject()
  }

  def build(json: XContentBuilder): Unit = {

    for ( all <- _all ) json.startObject("_all").field("enabled", all).endObject()
    for ( source <- _source ) json.startObject("_source").field("enabled", source).endObject()

    if (dynamic_date_formats.nonEmpty)
      json.field("dynamic_date_formats", dynamic_date_formats.toArray: _*)

    for ( dd <- date_detection ) json.field("date_detection", dd)
    for ( nd <- numeric_detection ) json.field("numeric_detection", nd)

    json.field("dynamic", _dynamic match {
      case Strict => "strict"
      case False => "false"
      case _ => "dynamic"
    })

    _boostName.foreach(arg =>
      json.startObject("_boost").field("name", arg).field("null_value", _boostValue).endObject()
    )

    _analyzer.foreach(arg => json.startObject("_analyzer").field("path", arg).endObject())

    _parent.foreach(arg => json.startObject("_parent").field("type", arg).endObject())

    if (_size) json.startObject("_size").field("enabled", true).endObject()

    _timestamp.foreach { timestamp =>
      json.startObject("_timestamp").field("enabled", timestamp.enabled)
      timestamp.path.foreach { path =>
        json.field("path", path)
      }
      timestamp.format.foreach { format =>
        json.field("format", format)
      }
      timestamp.default.foreach { default =>
        json.field("default", default)
      }
      json.endObject()
    }

    if (_useTtl)
      json.startObject("_ttl").field("enabled", _ttl).endObject()

    if (_fields.nonEmpty) {
      json.startObject("properties")
      for ( field <- _fields ) {
        field.build(json)
      }
      json.endObject() // end properties
    }

    if (_meta.size > 0) {
      json.startObject("_meta")
      for ( meta <- _meta ) {
        json.field(meta._1, meta._2)
      }
      json.endObject()
    }

    if (_routing.isDefined) {
      json.startObject("_routing").field("required", _routing.get.required)
      if (_routing.get.path.isDefined) {
        json.field("path", _routing.get.path.get)
      }
      json.endObject()
    }

    if (_templates.nonEmpty) {
      json.startArray("dynamic_templates")
      for ( template <- _templates ) template.build(json)
      json.endArray()
    }
  }
}
