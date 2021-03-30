package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.analyzers.{EdgeNGramTokenizer, KeywordTokenizer, NGramTokenizer, PathHierarchyTokenizer, PatternTokenizer, StandardTokenizer, UaxUrlEmailTokenizer}

@deprecated("use new analysis package", "7.7.0")
trait TokenizerApi {
  @deprecated("use new analysis package", "7.7.0")
  def edgeNGramTokenizer(name: String): EdgeNGramTokenizer = EdgeNGramTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def keywordTokenizer(name: String): KeywordTokenizer = KeywordTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def nGramTokenizer(name: String): NGramTokenizer = NGramTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def pathHierarchyTokenizer(name: String): PathHierarchyTokenizer = PathHierarchyTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def patternTokenizer(name: String): PatternTokenizer = PatternTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def standardTokenizer(name: String): StandardTokenizer = StandardTokenizer(name)
  @deprecated("use new analysis package", "7.7.0")
  def uaxUrlEmailTokenizer(name: String): UaxUrlEmailTokenizer = UaxUrlEmailTokenizer(name)
}
