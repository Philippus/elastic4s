package com.sksamuel.elastic4s.requests.analyzers

trait NormalizerApi {

  @deprecated("use new analysis package", "7.0.1")
  def customNormalizer(name: String): CustomNormalizerDefinition =
    CustomNormalizerDefinition(name)

  @deprecated("use new analysis package", "7.0.1")
  def customNormalizer(name: String, filter: AnalyzerFilter, rest: AnalyzerFilter*): CustomNormalizerDefinition =
    CustomNormalizerDefinition(name, filter +: rest)
}
