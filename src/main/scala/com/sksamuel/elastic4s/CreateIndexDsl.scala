package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }
import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.admin.indices.create.{ CreateIndexAction, CreateIndexRequest }
import com.sksamuel.elastic4s.mappings.MappingDefinition

/** @author Stephen Samuel */
trait CreateIndexDsl {

  def create = new CreateExpectsIndexOrSnapshot
  class CreateExpectsIndexOrSnapshot {
    def index(name: String) = new CreateIndexDefinition(name)
  }

  class IndexSettings(var shards: Int = 5, var replicas: Int = 1)

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*) = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*) = new TokenFiltersWrapper(filters)

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])

  class CreateIndexDefinition(name: String) extends IndicesRequestDefinition(CreateIndexAction.INSTANCE) {

    val _mappings = new ListBuffer[MappingDefinition]
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

      source.startObject("index")
      source.field("number_of_shards", _settings.shards)
      source.field("number_of_replicas", _settings.replicas)
      source.endObject()

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
    analyzers
      .filter(_.isInstanceOf[CustomAnalyzerDefinition])
      .map(_.asInstanceOf[CustomAnalyzerDefinition])
      .map(_.tokenizer)
      .filter(_.customized)

  def tokenFilterDefinitions: Iterable[TokenFilterDefinition] =
    analyzers
      .filter(_.isInstanceOf[CustomAnalyzerDefinition])
      .map(_.asInstanceOf[CustomAnalyzerDefinition])
      .flatMap(_.filters)
      .filter(_.isInstanceOf[TokenFilterDefinition])
      .map(_.asInstanceOf[TokenFilterDefinition])

  def charFilterDefinitions: Iterable[CharFilterDefinition] =
    analyzers
      .filter(_.isInstanceOf[CustomAnalyzerDefinition])
      .map(_.asInstanceOf[CustomAnalyzerDefinition])
      .flatMap(_.filters)
      .filter(_.isInstanceOf[CharFilterDefinition])
      .map(_.asInstanceOf[CharFilterDefinition])

}
