package com.sksamuel.elastic4s.anaylzers

trait TokenFilterDsl {
  def commonGramsTokenFilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)
  def ngramTokenFilter(name: String): NGramTokenFilter = NGramTokenFilter(name)
  def edgeNGramTokenFilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)
  def shingleTokenFilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)
  def snowballTokenFilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)
  def stemmerTokenFilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
}
