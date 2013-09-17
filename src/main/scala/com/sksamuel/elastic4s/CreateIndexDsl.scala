package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.admin.indices.create.{CreateIndexAction, CreateIndexRequest}

/** @author Stephen Samuel */
trait CreateIndexDsl {

  def create = new CreateIndexExpectsName
  class CreateIndexExpectsName {
    def index(name: String) = new CreateIndexDefinition(name)
  }

  implicit def map(`type`: String) = new MappingDefinition(`type`)
  def id: FieldDefinition = field("_id")
  implicit def field(name: String): FieldDefinition = new FieldDefinition(name)

  class MappingDefinition(val `type`: String) {
    var source = true
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
      _analyzer = Option(analyzer.definition.string())
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
      this.source = source
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

    def typed(ft: FieldType) = fieldType(ft)
    def fieldType(ft: FieldType) = {
      _type = Option(ft)
      this
    }

    def analyzer(a: Analyzer) = {
      _analyzer = Option(a)
      this
    }

    def store(store: Boolean) = {
      _store = store
      this
    }

    def ignoreAbove(ignoreAbove: Int) = {
      _ignoreAbove = Option(ignoreAbove)
      this
    }

    def boost(boost: Double) = {
      _boost = boost
      this
    }

    def omitNorms(omitNorms: Boolean) = {
      _omitNorms = Option(omitNorms)
      this
    }

    def includeInAll(includeInAll: Boolean) = {
      _includeInAll = Option(includeInAll)
      this
    }

    def nullValue(nullValue: String) = {
      _nullValue = Option(nullValue)
      this
    }

    def index(index: String) = {
      _index = Option(index)
      this
    }
  }

  class IndexSettings(var shards: Int = 5, var replicas: Int = 1)

  class CreateIndexDefinition(name: String) extends IndicesRequestDefinition(CreateIndexAction.INSTANCE) {

    val _mappings = new ListBuffer[MappingDefinition]
    val _settings = new IndexSettings

    def build = new CreateIndexRequest(name).source(_source)

    def shards(shards: Int) = {
      _settings.shards = shards
      this
    }

    def replicas(replicas: Int) = {
      _settings.replicas = replicas
      this
    }

    def mappings(mappings: MappingDefinition*) = {
      _mappings ++= mappings
      this
    }

    def _source: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()

      source.startObject("settings")
      source.field("number_of_shards", _settings.shards)
      source.field("number_of_replicas", _settings.replicas)
      source.endObject()

      if (_mappings.size > 0)
        source.startObject("mappings")

      for ( mapping <- _mappings ) {

        source.startObject(mapping.`type`)
        source.startObject("_source").field("enabled", mapping.source)
        source.field("dynamic_date_formats", mapping.dynamic_date_formats.toArray: _*)
        if (mapping.date_detection)
          source.field("date_detection", mapping.date_detection)
        if (mapping.numeric_detection)
          source.field("numeric_detection", mapping.numeric_detection)
        source.endObject()

        mapping._boostName
          .foreach(arg => source
          .startObject("_boost")
          .field("name", arg)
          .field("null_value", mapping._boostValue)
          .endObject())
        mapping._analyzer.foreach(arg => source.startObject("_analyzer").field("path", arg).endObject())
        if (mapping._size)
          source.startObject("_size").field("enabled", true).endObject()

        source.startObject("properties")
        for ( field <- mapping._fields ) {
          source.startObject(field.name)
          field._type.foreach(arg => source.field("type", arg.elastic))
          field._analyzer.foreach(arg => source.field("analyzer", arg.definition.string()))
          field._index.foreach(index => source.field("index", index))
          field._omitNorms.foreach(omitNorms => source.field("omit_norms", omitNorms))
          field._nullValue.foreach(nullValue => source.field("null_value", nullValue))
          field._includeInAll.foreach(includeInAll => source.field("include_in_all", includeInAll))
          if (field._boost > 0)
            source.field("boost", field._boost)
          source.field("store", field._store.toString)
          source.endObject()
        }
        source.endObject() // end properties

        if (mapping._meta.size > 0) {
          source.startObject("_meta")
          for ( meta <- mapping._meta ) {
            source.field(meta._1, meta._2)
          }
          source.endObject()
        }

        source.endObject() // end mapping name
      }

      if (_mappings.size > 0)
        source.endObject() // end mappings

      source.endObject()
    }
  }

}
