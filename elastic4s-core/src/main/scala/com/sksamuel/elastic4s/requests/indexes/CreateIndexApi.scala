package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, TokenFilter, Tokenizer}
import com.sksamuel.elastic4s.requests.mappings.{FieldDefinition, MappingDefinition}

trait CreateIndexApi {

  def createIndex(name: String): CreateIndexRequest = CreateIndexRequest(name)

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*)        = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*)            = new TokenFiltersWrapper(filters)

  @deprecated("Use of type is deprecated in 7; create the mapping without a type name, eg createIndex(\"foo\").mapping(mapping(fielda, fieldb))", "7.0.0")
  def mapping(name: String): MappingDefinition = MappingDefinition(name)

  def mapping(field: FieldDefinition, tail: FieldDefinition*): MappingDefinition = mapping(field +: tail)
  def mapping(fields: Seq[FieldDefinition]): MappingDefinition = MappingDefinition(fields)

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])
}
