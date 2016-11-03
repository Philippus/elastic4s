package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

class MappingDefinition(val `type`: String) {

  var _all: Option[Boolean] = None
  var _source: Option[Boolean] = None
  var _sourceExcludes: Iterable[String] = Nil
  var date_detection: Option[Boolean] = None
  var numeric_detection: Option[Boolean] = None
  var _size: Option[Boolean] = None
  var dynamic_date_formats: Iterable[String] = Nil
  val _fields = new ListBuffer[TypedFieldDefinition]
  var _analyzer: Option[String] = None
  var _boostName: Option[String] = None
  var _boostValue: Double = 0
  var _parent: Option[String] = None
  var _dynamic: Option[DynamicMapping] = None
  var _meta: Map[String, Any] = Map.empty
  var _routing: Option[RoutingDefinition] = None
  var _timestamp: Option[TimestampDefinition] = None
  var _templates: Iterable[DynamicTemplateDefinition] = Nil
  var _id: Option[IdField] = None

  def all(enabled: Boolean): this.type = {
    _all = Option(enabled)
    this
  }

  def id(idField: IdField): this.type = {
    _id = Some(idField)
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
    _dynamic = Option(dynamic)
    this
  }

  def timestamp(enabled: Boolean,
                path: Option[String] = None,
                format: Option[String] = None,
                default: Option[String] = None): this.type = {
    this._timestamp = Some(TimestampDefinition(enabled, path, format, default))
    this
  }

  def timestamp(timestampDefinition: TimestampDefinition): this.type = {
    this._timestamp = Option(timestampDefinition)
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

  def sourceExcludes(excludes: String*) = {
    this._sourceExcludes = excludes
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

  def fields(fields: Iterable[TypedFieldDefinition]): this.type = as(fields)
  def fields(fields: TypedFieldDefinition*): this.type = as(fields: _*)

  def as(fields: TypedFieldDefinition*): this.type = as(fields.toIterable)
  def as(iterable: Iterable[TypedFieldDefinition]): this.type = {
    _fields ++= iterable
    this
  }

  def size(size: Boolean): this.type = {
    _size = Option(size)
    this
  }

  def dynamicTemplates(temps: Iterable[DynamicTemplateDefinition]): this.type = templates(temps)
  def dynamicTemplates(temps: DynamicTemplateDefinition*): this.type = templates(temps)
  def templates(temps: Iterable[DynamicTemplateDefinition]): this.type = templates(temps.toSeq: _*)
  def templates(temps: DynamicTemplateDefinition*): this.type = {
    _templates = temps
    this
  }

  def build: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    build(builder)
    builder.endObject()
  }

  // returns the mapping json wrapped in the mapping type name, eg "mytype" : { mapping }
  def buildWithName: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    builder.startObject(`type`)
    build(builder)
    builder.endObject()
    builder.endObject()
  }

  def build(json: XContentBuilder): Unit = {

    for ( all <- _all ) json.startObject("_all").field("enabled", all).endObject()
    (_source, _sourceExcludes) match {
      case (_, l) if l.nonEmpty => json.startObject("_source").field("excludes", l.asJava).endObject()
      case (Some(source), _) => json.startObject("_source").field("enabled", source).endObject()
      case _ =>
    }

    if (dynamic_date_formats.nonEmpty)
      json.field("dynamic_date_formats", dynamic_date_formats.asJava)

    for ( dd <- date_detection ) json.field("date_detection", dd)
    for ( nd <- numeric_detection ) json.field("numeric_detection", nd)

    _dynamic.foreach(dynamic => {
      json.field("dynamic", dynamic match {
        case DynamicMapping.Strict => "strict"
        case DynamicMapping.False => "false"
        case _ => "dynamic"
      })
    })

    _boostName.foreach(x => json.startObject("_boost").field("name", x).field("null_value", _boostValue).endObject())
    _analyzer.foreach(x => json.startObject("_analyzer").field("path", x).endObject())
    _parent.foreach(x => json.startObject("_parent").field("type", x).endObject())
    _size.foreach(x => json.startObject("_size").field("enabled", x).endObject())

    _timestamp.foreach(_.build(json))

    if (_fields.nonEmpty) {
      json.startObject("properties")
      for ( field <- _fields ) {
        field.build(json)
      }
      json.endObject() // end properties
    }

    for ( id <- _id ) {
      json.startObject("_id")
      json.field("index", id.index)
      json.endObject()
    }

    if (_meta.nonEmpty) {
      json.startObject("_meta")
      for ( meta <- _meta ) {
        json.field(meta._1, meta._2)
      }
      json.endObject()
    }

    _routing.foreach(routing => {
      json.startObject("_routing").field("required", routing.required)
      routing.path.foreach(path => json.field("path", path))
      json.endObject()
    })

    if (_templates.nonEmpty) {
      json.startArray("dynamic_templates")
      for ( template <- _templates ) template.build(json)
      json.endArray()
    }
  }
}

case class IdField(index: String)
