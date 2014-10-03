package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }

import scala.collection.mutable

/** @author Stephen Samuel */
trait CreateIndexDsl {

  def create = new CreateExpectsIndexOrSnapshot
  class CreateExpectsIndexOrSnapshot {
    def index(name: String) = new CreateIndexDefinition(name)
  }

  class IndexSettings {
    private val ShardsKey = "number_of_shards"
    private val ReplicasKey = "number_of_replicas"
    private val RefreshIntervalKey = "refresh_interval"

    val settings: mutable.Map[String, Any] = mutable.Map()

    def shards: Option[Int] = settings.get(ShardsKey).map(_.asInstanceOf[Int])
    def shards_=(s: Int): Unit = settings += ShardsKey -> s

    def replicas: Option[Int] = settings.get(ReplicasKey).map(_.asInstanceOf[Int])
    def replicas_=(r: Int): Unit = settings += ReplicasKey -> r

    def refreshInterval: Option[String] = settings.get(RefreshIntervalKey).map(_.asInstanceOf[String])
    def refreshInterval_=(i: String): Unit = settings += RefreshIntervalKey -> i
  }

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*) = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*) = new TokenFiltersWrapper(filters)

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])

  class CreateIndexDefinition(name: String) {

    val _mappings = new mutable.ListBuffer[MappingDefinition]
    val _settings = new IndexSettings
    var _analysis: Option[AnalysisDefinition] = None

    def build = new CreateIndexRequest(name).source(_source)

    def shards(shards: Int): CreateIndexDefinition = {
      _settings.shards = shards
      this
    }

    def replicas(replicas: Int): CreateIndexDefinition = {
      _settings.replicas = replicas
      this
    }

    def refreshInterval(interval: String): CreateIndexDefinition = {
      _settings.refreshInterval = interval
      this
    }

    def indexSetting(name: String, value: Any): CreateIndexDefinition = {
      _settings.settings += name -> value
      this
    }

    def mappings(mappings: MappingDefinition*): CreateIndexDefinition = {
      _mappings ++= mappings
      this
    }

    def analysis(analyzers: AnalyzerDefinition*) = {
      _analysis = Some(new AnalysisDefinition(analyzers))
      this
    }

    def _source: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()

      source.startObject("settings")

      if (_settings.settings.size > 0) {
        source.startObject("index")

        _settings.settings foreach {
          case (key, value) =>
            source.field(key, value)
        }

        source.endObject()
      }

      _analysis.foreach(analysis => {
        source.startObject("analysis")

        val charFilterDefinitions = analysis.charFilterDefinitions
        if (charFilterDefinitions.size > 0) {
          source.startObject("char_filter")
          charFilterDefinitions.foreach { filter =>
            source.startObject(filter.name)
            source.field("type", filter.filterType)
            filter.build(source)
            source.endObject()
          }
          source.endObject()
        }

        source.startObject("analyzer")
        analysis.analyzers.foreach(analyzer => {
          source.startObject(analyzer.name)
          analyzer.build(source)
          source.endObject()
        })
        source.endObject()

        val tokenizers = analysis.tokenizers
        if (tokenizers.size > 0) {
          source.startObject("tokenizer")
          tokenizers.foreach(tokenizer => {
            source.startObject(tokenizer.name)
            tokenizer.build(source)
            source.endObject()
          })
          source.endObject()
        }

        val tokenFilterDefinitions = analysis.tokenFilterDefinitions
        if (tokenFilterDefinitions.size > 0) {
          source.startObject("filter")
          tokenFilterDefinitions.foreach(filter => {
            source.startObject(filter.name)
            source.field("type", filter.filterType)
            filter.build(source)
            source.endObject()
          })
          source.endObject()
        }

        source.endObject()
      })

      source.endObject() // end settings

      if (_mappings.size > 0) {
        source.startObject("mappings")
        for (mapping <- _mappings) {
          mapping.build(source)
        }
        source.endObject()
      }

      source.endObject()
    }
  }
}

class AnalysisDefinition(val analyzers: Iterable[AnalyzerDefinition]) {

  def tokenizers: Iterable[Tokenizer] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.map(_.tokenizer).filter(_.customized)

  def tokenFilterDefinitions: Iterable[TokenFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case token: TokenFilterDefinition => token
    }

  def charFilterDefinitions: Iterable[CharFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case char: CharFilterDefinition => char
    }
}
