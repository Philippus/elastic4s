package com.sksamuel.elastic4s.searches.suggestion

trait SuggestionDefinition {

  def name: String
  def fieldname: String

  def size: Option[Int]
  def shardSize: Option[Int]
  def text: Option[String]
  def analyzer: Option[String]

  def size(size: Int): SuggestionDefinition
  def shardSize(shardSize: Int): SuggestionDefinition
  def text(text: String): SuggestionDefinition
  def analyzer(analyzer: String): SuggestionDefinition
}
