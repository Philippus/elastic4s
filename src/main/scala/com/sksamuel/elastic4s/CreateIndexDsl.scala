package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest

/** @author Stephen Samuel */
trait CreateIndexDsl {

    def create = new CreateIndexExpectsName
    class CreateIndexExpectsName {
        def index(name: String) = new CreateIndexDefinition(name)
    }

    implicit def map(name: String) = new MappingTypeExpectsDefinition(name, false, Nil)

    def id: FieldsBuilder = field("_id")
    implicit def field(name: String): FieldsBuilder = new FieldsBuilder(List(new FieldMapping(name)))

    class MappingTypeExpectsDefinition(`type`: String, source: Boolean, mappings: List[Mapping]) {
        def source(source: Boolean) = new MappingTypeExpectsDefinition(`type`, source, mappings)
        def as(block: => FieldsBuilder): MappingsBuilder = {
            new MappingsBuilder(mappings :+ new Mapping(`type`, source, block.fields))
        }
    }

    class MappingsBuilder(val mappings: List[Mapping]) {
        def and(`type`: String) = new MappingTypeExpectsDefinition(`type`, false, mappings)
    }

    class FieldsBuilder(val fields: List[FieldMapping]) {

        def fieldType(ft: FieldType) = {
            fields.last.`type` = Option(ft)
            this
        }

        def analyzer(a: Analyzer) = {
            fields.last.analyzer = Option(a)
            this
        }

        def store(store: Boolean) = {
            fields.last.store = true
            this
        }

        def and(name: String) = new FieldsBuilder(fields :+ new FieldMapping(name))
    }

    class FieldMapping(val name: String) {
        var `type`: Option[FieldType] = None
        var analyzer: Option[Analyzer] = None
        var store: Boolean = false
    }

    class Mapping(val name: String, var source: Boolean, val fields: List[FieldMapping]) {

    }

    class IndexSettings(var shards: Int = 1, var replicas: Int = 1)

    class CreateIndexDefinition(name: String) {

        val _mappings = new ListBuffer[Mapping]
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

        def mappings(block: => MappingsBuilder) = {
            _mappings ++= block.mappings
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

                source.startObject(mapping.name)
                source.startObject("_source").field("enabled", mapping.source).endObject()
                source.startObject("properties")

                for ( field <- mapping.fields ) {
                    source.startObject(field.name)
                    field.`type`.foreach(arg => source.field("type", arg.elastic))
                    field.analyzer.foreach(arg => source.field("index", arg.elastic))
                    source.field("store", field.store.toString)
                    source.endObject()
                }

                source.endObject() // end properties
                source.endObject() // end mapping name
            }

            if (_mappings.size > 0)
                source.endObject() // end mappings

            source.endObject()
        }
    }

}
