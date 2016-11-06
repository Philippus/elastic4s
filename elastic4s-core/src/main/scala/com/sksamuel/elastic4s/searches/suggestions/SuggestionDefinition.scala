package com.sksamuel.elastic4s.searches.suggestions

import org.elasticsearch.search.suggest.SuggestionBuilder

trait SuggestionDefinition {

  type B <: SuggestionBuilder[B]

  def builder: B

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

  def populate(builder: B): Unit = {
    analyzer.foreach(builder.analyzer)
    shardSize.foreach(builder.shardSize(_))
    size.foreach(builder.size)
    text.foreach(builder.text)
  }
}