package com.sksamuel.elastic4s.requests.analyzers

trait TokenizerApi {
  def edgeNGramTokenizer(name: String): EdgeNGramTokenizer         = EdgeNGramTokenizer(name)
  def keywordTokenizer(name: String): KeywordTokenizer             = KeywordTokenizer(name)
  def nGramTokenizer(name: String): NGramTokenizer                 = NGramTokenizer(name)
  def pathHierarchyTokenizer(name: String): PathHierarchyTokenizer = PathHierarchyTokenizer(name)
  def patternTokenizer(name: String): PatternTokenizer             = PatternTokenizer(name)
  def standardTokenizer(name: String): StandardTokenizer           = StandardTokenizer(name)
  def uaxUrlEmailTokenizer(name: String): UaxUrlEmailTokenizer     = UaxUrlEmailTokenizer(name)
}
