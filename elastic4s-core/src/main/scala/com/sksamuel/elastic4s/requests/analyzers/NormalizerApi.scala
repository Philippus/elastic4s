package com.sksamuel.elastic4s.requests.analyzers

trait NormalizerApi {

  def customNormalizer(name: String): CustomNormalizerDefinition =
    CustomNormalizerDefinition(name)

  def customNormalizer(name: String, filter: AnalyzerFilter, rest: AnalyzerFilter*): CustomNormalizerDefinition =
    CustomNormalizerDefinition(name, filter +: rest)
}
