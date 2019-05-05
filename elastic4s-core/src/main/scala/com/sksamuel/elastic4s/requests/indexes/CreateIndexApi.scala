package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, TokenFilter, Tokenizer}
import com.sksamuel.elastic4s.requests.mappings.{FieldDefinition, MappingDefinition}

trait CreateIndexApi {

  def createIndex(name: String): CreateIndexRequest = CreateIndexRequest(name)

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*)        = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*)            = new TokenFiltersWrapper(filters)

  @deprecated("Use of type is deprecated in 7; create the mapping without a type name, eg createIndex(\"foo\").mapping(properties(fielda, fieldb))", "7.0.0")
  def mapping(name: String): MappingDefinition = MappingDefinition(Some(name))

  val emptyMapping: MappingDefinition = MappingDefinition.empty

  def properties(field: FieldDefinition, tail: FieldDefinition*): MappingDefinition = mapping(field +: tail)
  def properties(fields: Seq[FieldDefinition] = Nil): MappingDefinition = MappingDefinition(fields)

  @deprecated("This method is now called properties as types are deprecated in 7.0", "7.0.0")
  def mapping(field: FieldDefinition, tail: FieldDefinition*): MappingDefinition = mapping(field +: tail)

  @deprecated("This method is now called properties as types are deprecated in 7.0", "7.0.0")
  def mapping(fields: Seq[FieldDefinition] = Nil): MappingDefinition = MappingDefinition(fields)

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])
}
