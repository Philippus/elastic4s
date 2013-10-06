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
      source.field("number_of_shards", _settings.shards)
      source.field("number_of_replicas", _settings.replicas)
      source.endObject()

      if (_mappings.size > 0) source.startObject("mappings")
      for ( mapping <- _mappings ) {
        mapping.build(source)
      }
      if (_mappings.size > 0) source.endObject()

      _analysis.foreach(analysis => {
        source.startObject("analysis")

        source.startObject("analyzer")
        analysis.analyzers.foreach(_.build(source))
        source.endObject()

        source.startObject("tokenizer")
        analysis.tokenizers.foreach(_.build(source))
        source.endObject()

        source.startObject("filter")
        analysis.filters.foreach(_.build(source))
        source.endObject()

        source.endObject()
      })

      source.endObject()
    }
  }
}

class AnalysisDefinition(val analyzers: Iterable[AnalyzerDefinition]) {
  def tokenizers: Iterable[Tokenizer] = Nil
  def filters: Iterable[Tokenizer] = Nil
}
