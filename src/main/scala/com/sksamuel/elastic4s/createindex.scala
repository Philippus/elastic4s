package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.util.DynamicVariable
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
case class CreateIndexReq(name: String, settings: IndexSettings, mappings: Seq[Mapping] = Nil) {

    def build(client: Client): CreateIndexRequestBuilder = {
        client.client.admin().indices().prepareCreate(name).setSource(_source)
    }

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

case class IndexSettings(shards: Int = 1, replicas: Int = 1)
case class Mapping(name: String, fields: Seq[FieldMapping] = Nil, source: Boolean = true)
case class FieldMapping(name: String, `type`: Option[FieldType] = None, analyzer: Option[Analyzer] = None, store: Boolean = false)

trait CreateIndexDsl {

    private val createIndexBuilderContext = new DynamicVariable[Option[CreateIndexBuilder]](None)
    private val mappingBuilderContext = new DynamicVariable[Option[MappingBuilder]](None)

    def createIndex(name: String)(block: => Unit): CreateIndexReq = {
        val builder = new CreateIndexBuilder(name)
        createIndexBuilderContext.withValue(Some(builder)) {
            block
        }
        builder.build
    }

    def shards(shards: Int) {
        createIndexBuilderContext.value.foreach(_.shards = shards)
    }

    def replicas(replicas: Int) {
        createIndexBuilderContext.value.foreach(_.replicas = replicas)
    }

    def mappings(block: => Unit) {
        block
    }

    def mapping(name: String)(block: => Unit) {
        val mb = new MappingBuilder(name)
        createIndexBuilderContext.value.foreach(_.mappingBuilders.append(mb))
        mappingBuilderContext.withValue(Some(mb)) {
            block
        }
    }

    def source(enabled: Boolean) {
        mappingBuilderContext.value.foreach(_.source = enabled)
    }

    def id: FieldBuilder = field("_id")
    def field(name: String): FieldBuilder = {
        val fb = new FieldBuilder(name)
        mappingBuilderContext.value.foreach(_.fieldBuilders.append(fb))
        fb
    }
}

class MappingBuilder(name: String) {
    var source: Boolean = false
    val fieldBuilders = ListBuffer[FieldBuilder]()
    def build: Mapping = {
        val fields = fieldBuilders.map(_.build).toSeq
        Mapping(name, fields, source)
    }
}

class FieldBuilder(name: String) {

    var _fieldType: Option[FieldType] = None
    var _analyzer: Option[Analyzer] = None
    var _store: Boolean = false

    def fieldType(ft: FieldType) = {
        _fieldType = Option(ft)
        this
    }

    def analyzer(a: Analyzer) = {
        _analyzer = Option(a)
        this
    }

    def store = {
        _store = true
        this
    }

    def build = FieldMapping(name, _fieldType, _analyzer, _store)
}

class CreateIndexBuilder(name: String) {

    var shards: Int = 0
    var replicas: Int = 0
    val mappingBuilders = ListBuffer[MappingBuilder]()

    def build: CreateIndexReq = {
        val mappings = mappingBuilders.map(_.build)
        CreateIndexReq(name, IndexSettings(shards, replicas), mappings)
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
