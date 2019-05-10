package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.{AnalyzerDefinition, CharFilterDefinition, CustomAnalyzerDefinition, CustomNormalizerDefinition, NormalizerDefinition, TokenFilterDefinition, Tokenizer}

@deprecated("use new analysis package", "7.0.1")
case class AnalysisDefinition(analyzers: Iterable[AnalyzerDefinition],
                              normalizers: Iterable[NormalizerDefinition]) {

  @deprecated("use new analysis package", "7.0.1")
  def tokenizers: Iterable[Tokenizer] =
    analyzers
      .collect {
        case custom: CustomAnalyzerDefinition => custom
      }
      .map(_.tokenizer)
      .filter(_.customized)

  @deprecated("use new analysis package", "7.0.1")
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

  @deprecated("use new analysis package", "7.0.1")
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
