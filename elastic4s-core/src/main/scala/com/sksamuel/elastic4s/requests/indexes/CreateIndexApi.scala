package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, TokenFilter, Tokenizer}
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition

trait CreateIndexApi {

  def createIndex(name: String): CreateIndexRequest = CreateIndexRequest(name)

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*)        = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*)            = new TokenFiltersWrapper(filters)

  @deprecated("types are deprecated now", "7.0")
  def mapping(name: String): MappingDefinition = MappingDefinition(name)

  def mapping(): MappingDefinition = MappingDefinition()

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])
}
