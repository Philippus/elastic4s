package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.fields.ElasticField
import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, TokenFilter, Tokenizer}
import com.sksamuel.elastic4s.requests.indexes.CreateIndexRequest
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition

trait CreateIndexApi {

  def createIndex(name: String): CreateIndexRequest = CreateIndexRequest(name)

  @deprecated("use new analysis package", "7.0.1")
  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)

  @deprecated("use new analysis package", "7.0.1")
  def tokenizers(tokenizers: Tokenizer*) = new TokenizersWrapper(tokenizers)

  @deprecated("use new analysis package", "7.0.1")
  def filters(filters: TokenFilter*) = new TokenFiltersWrapper(filters)

  val emptyMapping: MappingDefinition = MappingDefinition()

  def properties(fields: Seq[ElasticField] = Nil): MappingDefinition          = MappingDefinition(fields)
  def properties(field: ElasticField, tail: ElasticField*): MappingDefinition =
    MappingDefinition(properties = field +: tail)

  @deprecated("This method is now called properties as types are deprecated in 7.0", "7.0.0")
  def mapping(field: ElasticField, tail: ElasticField*): MappingDefinition = properties(field +: tail)

  @deprecated("This method is now called properties as types are deprecated in 7.0", "7.0.0")
  def mapping(fields: Seq[ElasticField] = Nil): MappingDefinition = MappingDefinition(fields)

  @deprecated("use new analysis package", "7.0.1")
  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])

  @deprecated("use new analysis package", "7.0.1")
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])

  @deprecated("use new analysis package", "7.0.1")
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])
}
