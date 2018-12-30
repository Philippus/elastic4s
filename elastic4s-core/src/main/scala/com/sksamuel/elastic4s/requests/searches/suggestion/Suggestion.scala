package com.sksamuel.elastic4s.requests.searches.suggestion

trait Suggestion {

  def name: String
  def fieldname: String

  def size: Option[Int]
  def shardSize: Option[Int]
  def text: Option[String]
  def analyzer: Option[String]

  def size(size: Int): Suggestion
  def shardSize(shardSize: Int): Suggestion
  def text(text: String): Suggestion
  def analyzer(analyzer: String): Suggestion
}
