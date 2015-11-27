package com.sksamuel.elastic4s.analyzers

trait TokenFilterDsl {
  def commonGramsTokenFilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)
  def edgeNGramTokenFilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)
  def elisionTokenFilter(name: String): ElisionTokenFilter = ElisionTokenFilter(name)
  def keywordMarkerTokenFilter(name: String): KeywordMarkerTokenFilter = KeywordMarkerTokenFilter(name)
  def limitTokenFilter(name: String): LimitTokenFilter = LimitTokenFilter(name)
  def lengthTokenFilter(name: String): LengthTokenFilter = LengthTokenFilter(name)
  def ngramTokenFilter(name: String): NGramTokenFilter = NGramTokenFilter(name)
  def shingleTokenFilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)
  def snowballTokenFilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)
  def stemmerTokenFilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
  def stopTokenFilter(name: String): StopTokenFilter = StopTokenFilter(name)
  def wordDelimiterTokenFilter(name: String): WordDelimiterTokenFilter = WordDelimiterTokenFilter(name)
}
