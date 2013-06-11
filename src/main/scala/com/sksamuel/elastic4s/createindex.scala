package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
case class CreateIndexReq(name: String, settings: IndexSettings, mappings: Seq[Mapping] = Nil) {

    def build(client: Client): CreateIndexRequestBuilder = {
        client.client.admin().indices().prepareCreate(name).setSource(_source)
    }

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()

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

case class IndexSettings(shards: Int = 1, replicas: Int = 1)
case class Mapping(name: String, fields: Seq[FieldMapping] = Nil, source: Boolean = true)
case class FieldMapping(name: String, `type`: Option[FieldType] = None, analyzer: Option[Analyzer] = None, store: Boolean = false)

object CreateIndexReq {
    def apply(name: String) = new CreateIndexReqBuilder(name)

}
class CreateIndexReqBuilder(name: String) {

    val mappings = ListBuffer[Mapping]()
    var settings = IndexSettings()

    def shards(shards: Int) = {
        settings = settings.copy(shards = shards)
        this
    }
    def replicas(replicas: Int) = {
        settings = settings.copy(replicas = replicas)
        this
    }
    def mapping(name: String) = new MappingBuilder(name, this)
    def build: CreateIndexReq = CreateIndexReq(name, settings, mappings.toSeq)
}

class MappingBuilder(name: String, val parent: CreateIndexReqBuilder) {

    var _source: Boolean = true
    val fields = ListBuffer[FieldMapping]()

    def source(enable: Boolean) = {
        _source = enable
        this
    }

    def id = field("_id")
    def field(name: String) = new FieldMappingBuilder(name, this)

    def build: CreateIndexReq = {
        val mapping = Mapping(name, fields, _source)
        parent.mappings.append(mapping)
        parent.build
    }
}

class FieldMappingBuilder(name: String, parent: MappingBuilder) {

    var field = new FieldMapping(name)

    def fieldType(t: FieldType) = {
        field = field.copy(`type` = Option(t))
        this
    }

    def analyzer(a: Analyzer) = {
        field = field.copy(analyzer = Option(a))
        this
    }

    def store = {
        field = field.copy(store = true)
        this
    }

    def field(name: String) = {
        _end()
        parent.field(name)
    }

    def mapping(name: String) = {
        _end()
        parent.parent.mapping(name)
    }

    def build: CreateIndexReq = {
        _end()
        parent.build
    }

    def _end() {
        parent.fields.append(field)
    }
}

sealed trait Analyzer
object Analyzer {
    case object NotAnalyzed extends Analyzer
    case object WhitespaceAnalyzer extends Analyzer
    case object StandardAnalyzer extends Analyzer
    case object SimpleAnalyzer extends Analyzer
    case object StopAnalyzer extends Analyzer
    case object KeywordAnalyzer extends Analyzer
    case object PatternAnalyzer extends Analyzer
    case object SnowballAnalyzer extends Analyzer
}

sealed trait Tokenizer
object Tokenizer {
    case object KeywordTokenizer extends Tokenizer
    case object WhitespaceTokenizer extends Tokenizer
    case object StandardTokenizer extends Tokenizer
    case object LetterTokenizer extends Tokenizer
}
