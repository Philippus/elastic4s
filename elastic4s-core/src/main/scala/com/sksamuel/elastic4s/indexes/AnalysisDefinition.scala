package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers._

case class AnalysisDefinition(analyzers: Iterable[AnalyzerDefinition]) {

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
