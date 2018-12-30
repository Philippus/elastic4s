package com.sksamuel.elastic4s.requests.analyzers

trait TokenFilterApi {

  def commonGramsTokenFilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)

  def edgeNGramTokenFilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)

  def elisionTokenFilter(name: String): ElisionTokenFilter = ElisionTokenFilter(name)

  def keywordMarkerTokenFilter(name: String): KeywordMarkerTokenFilter = KeywordMarkerTokenFilter(name)

  def limitTokenCountTokenFilter(name: String): LimitTokenCountTokenFilter = LimitTokenCountTokenFilter(name)

  def lengthTokenFilter(name: String): LengthTokenFilter = LengthTokenFilter(name)

  def patternCaptureTokenFilter(name: String): PatternCaptureTokenFilter = PatternCaptureTokenFilter(name)

  def patternReplaceTokenFilter(name: String, pattern: String, replacement: String): PatternReplaceTokenFilter =
    PatternReplaceTokenFilter(name, pattern, replacement)

  def ngramTokenFilter(name: String): NGramTokenFilter = NGramTokenFilter(name)

  def shingleTokenFilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)

  def snowballTokenFilter(name: String, language: String): SnowballTokenFilter = SnowballTokenFilter(name, language)

  def stemmerOverrideTokenFilter(name: String): StemmerOverrideTokenFilter = StemmerOverrideTokenFilter(name)

  def stemmerTokenFilter(name: String, language: String): StemmerTokenFilter = StemmerTokenFilter(name, language)

  def stopTokenFilter(name: String): StopTokenFilter = StopTokenFilter(name)

  def synonymTokenFilter(name: String): SynonymTokenFilter = SynonymTokenFilter(name)

  def synonymTokenFilter(name: String, synonyms: Iterable[String]): SynonymTokenFilter =
    SynonymTokenFilter(name, synonyms = synonyms.toSet)

  def truncateTokenFilter(name: String): TruncateTokenFilter = TruncateTokenFilter(name)

  def uniqueTokenFilter(name: String): UniqueTokenFilter = UniqueTokenFilter(name)

  def wordDelimiterTokenFilter(name: String): WordDelimiterTokenFilter = WordDelimiterTokenFilter(name)

  def compoundWordTokenFilter(name: String, `type`: CompoundWordTokenFilterType) =
    CompoundWordTokenFilter(name, `type`)
}
