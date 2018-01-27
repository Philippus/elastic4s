package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers._

case class AnalysisDefinition(analyzers: Iterable[AnalyzerDefinition], normalizers: Iterable[NormalizerDefinition]) {

  def tokenizers: Iterable[Tokenizer] =
    analyzers
      .collect {
        case custom: CustomAnalyzerDefinition => custom
      }
      .map(_.tokenizer)
      .filter(_.customized)

  def tokenFilterDefinitions: Iterable[TokenFilterDefinition] = {
    val fromAnalyzers = analyzers
      .collect {
        case custom: CustomAnalyzerDefinition => custom
      }
      .flatMap(_.filters)
    val fromNormalizers = normalizers
      .collect {
        case custom: CustomNormalizerDefinition => custom
      }
      .flatMap(_.filters)

    (fromAnalyzers ++ fromNormalizers).collect {
      case token: TokenFilterDefinition => token
    }
  }

  def charFilterDefinitions: Iterable[CharFilterDefinition] = {
    val fromAnalyzers = analyzers
      .collect {
        case custom: CustomAnalyzerDefinition => custom
      }
      .flatMap(_.filters)
    val fromNormalizers = normalizers
      .collect {
        case custom: CustomNormalizerDefinition => custom
      }
      .flatMap(_.filters)

    (fromAnalyzers ++ fromNormalizers).collect {
      case char: CharFilterDefinition => char
    }
  }
}
