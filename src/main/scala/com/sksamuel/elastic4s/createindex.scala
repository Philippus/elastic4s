package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.immutable.Stack
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */

object CreateIndexDsl {

    def create = new CreateIndexExpectsName
    class CreateIndexExpectsName {
        def index(name: String) = new CreateIndexBuilder(name)
    }

    implicit def map(name: String) = new MappingTypeExpectsDefinition(name, false, Stack())

    def id: FieldsBuilder = field("_id")
    implicit def field(name: String): FieldsBuilder = new FieldsBuilder(Stack(new FieldMapping(name)))
}

class MappingTypeExpectsDefinition(`type`: String, source: Boolean, mappings: Stack[Mapping]) {
    def source(source: Boolean) = new MappingTypeExpectsDefinition(`type`, source, mappings)
    def as(block: => FieldsBuilder): MappingsBuilder = new MappingsBuilder(mappings push new Mapping(`type`, source))
}

class MappingsBuilder(mappings: Stack[Mapping]) {
    def as(block: => FieldsBuilder): MappingsBuilder = {
        block.fields.foreach(field => {
            mappings.last.fields.append(field)
        })
        this
    }
    def and(`type`: String) = new MappingTypeExpectsDefinition(`type`, false, mappings)
}

class FieldsBuilder(val fields: Stack[FieldMapping]) {

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

class Mapping(val name: String, var source: Boolean) {
    val fields = new ListBuffer[FieldMapping]
}

class CreateIndexBuilder(name: String) {

    var req = new CreateIndexReq(name, settings = IndexSettings())

    def shards(shards: Int) = {
        req = req.copy(settings = req.settings.copy(shards = shards))
        this
    }

    def replicas(replicas: Int) = {
        req = req.copy(settings = req.settings.copy(replicas = replicas))
        this
    }

    def mappings(block: => MappingsBuilder) = {
        this
    }
}

case class IndexSettings(shards: Int = 1, replicas: Int = 1)

case class CreateIndexReq(name: String, settings: IndexSettings = IndexSettings(), mappings: Seq[Mapping] = Nil) {

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()

        source.startObject("settings")
        source.field("number_of_shards", settings.shards)
        source.field("number_of_replicas", settings.replicas)
        source.endObject()

        if (mappings.size > 0)
            source.startObject("mappings")
        for ( mapping <- mappings ) {

            source.startObject(mapping.name)
            source.startObject("_source").field("enabled", mapping.source).endObject()
            source.startObject("properties")

            for ( field <- mapping.fields ) {
                source.startObject(field.name)
                if (field.`type`.isDefined)
                    source.field("type", field.`type`.get.toString)
                if (field.analyzer.isDefined)
                    source.field("index", field.analyzer.get.toString)
                source.field("store", field.store.toString)
                source.endObject()
            }

            source.endObject() // end properties
            source.endObject() // end mapping name
        }

        if (mappings.size > 0)
            source.endObject() // end mappings

        source.endObject()
    }
}


