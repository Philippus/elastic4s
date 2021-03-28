package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.analyzers.{CommonGramsTokenFilter, CompoundWordTokenFilter, CompoundWordTokenFilterType, EdgeNGramTokenFilter, ElisionTokenFilter, KeywordMarkerTokenFilter, LengthTokenFilter, LimitTokenCountTokenFilter, NGramTokenFilter, PatternCaptureTokenFilter, PatternReplaceTokenFilter, ShingleTokenFilter, SnowballTokenFilter, StemmerOverrideTokenFilter, StemmerTokenFilter, StopTokenFilter, SynonymTokenFilter, TruncateTokenFilter, UniqueTokenFilter, WordDelimiterTokenFilter}

@deprecated("use new analysis package", "7.7.0")
trait TokenFilterApi {

  @deprecated("use new analysis package", "7.7.0")
  def commonGramsTokenFilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def edgeNGramTokenFilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def elisionTokenFilter(name: String): ElisionTokenFilter = ElisionTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def keywordMarkerTokenFilter(name: String): KeywordMarkerTokenFilter = KeywordMarkerTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def limitTokenCountTokenFilter(name: String): LimitTokenCountTokenFilter = LimitTokenCountTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def lengthTokenFilter(name: String): LengthTokenFilter = LengthTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def patternCaptureTokenFilter(name: String): PatternCaptureTokenFilter = PatternCaptureTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def patternReplaceTokenFilter(name: String, pattern: String, replacement: String): PatternReplaceTokenFilter =
    PatternReplaceTokenFilter(name, pattern, replacement)

  @deprecated("use new analysis package", "7.7.0")
  def ngramTokenFilter(name: String): NGramTokenFilter = NGramTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def shingleTokenFilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def snowballTokenFilter(name: String, language: String): SnowballTokenFilter = SnowballTokenFilter(name, language)

  @deprecated("use new analysis package", "7.7.0")
  def stemmerOverrideTokenFilter(name: String): StemmerOverrideTokenFilter = StemmerOverrideTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def stemmerTokenFilter(name: String, language: String): StemmerTokenFilter = StemmerTokenFilter(name, language)

  @deprecated("use new analysis package", "7.7.0")
  def stopTokenFilter(name: String): StopTokenFilter = StopTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def synonymTokenFilter(name: String): SynonymTokenFilter = SynonymTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def synonymTokenFilter(name: String, synonyms: Iterable[String]): SynonymTokenFilter =
    SynonymTokenFilter(name, synonyms = synonyms.toSet)

  @deprecated("use new analysis package", "7.7.0")
  def truncateTokenFilter(name: String): TruncateTokenFilter = TruncateTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def uniqueTokenFilter(name: String): UniqueTokenFilter = UniqueTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def wordDelimiterTokenFilter(name: String): WordDelimiterTokenFilter = WordDelimiterTokenFilter(name)

  @deprecated("use new analysis package", "7.7.0")
  def compoundWordTokenFilter(name: String, `type`: CompoundWordTokenFilterType) =
    CompoundWordTokenFilter(name, `type`)
}
