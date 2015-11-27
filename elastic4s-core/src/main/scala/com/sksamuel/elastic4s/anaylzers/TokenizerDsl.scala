package com.sksamuel.elastic4s.analyzers

trait TokenizerDsl {
  def edgeNGramTokenizer(name: String): EdgeNGramTokenizer = EdgeNGramTokenizer(name)
  def standardTokenizer(name: String): StandardTokenizer = StandardTokenizer(name)
  def patternTokenizer(name: String): PatternTokenizer = PatternTokenizer(name)
  def keywordTokenizer(name: String): KeywordTokenizer = KeywordTokenizer(name)
  def pathHierarchyTokenizer(name: String): PathHierarchyTokenizer = PathHierarchyTokenizer(name)
  def nGramTokenizer(name: String): NGramTokenizer = NGramTokenizer(name)
  def uaxUrlEmailTokenizer(name: String): UaxUrlEmailTokenizer = UaxUrlEmailTokenizer(name)
}
