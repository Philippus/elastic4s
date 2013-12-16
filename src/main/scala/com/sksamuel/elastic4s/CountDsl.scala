package com.sksamuel.elastic4s

import org.elasticsearch.action.count.{CountAction, CountRequestBuilder}
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

/** @author Stephen Samuel */
trait CountDsl {

  @deprecated("use count", "1.0")
  def countall = new CountExpectsIndex

  def count = new CountExpectsIndex
  class CountExpectsIndex {
    def from(indexes: Iterable[String]): CountDefinition = new CountDefinition(indexes.toSeq)
    def from(indexes: String*): CountDefinition = new CountDefinition(indexes)
    def from(tuple: (String, String)): CountDefinition = new CountDefinition(Seq(tuple._1)).types(tuple._2)
  }

  class CountDefinition(indexes: Seq[String]) extends RequestDefinition(CountAction.INSTANCE) {

    private val _builder = new CountRequestBuilder(null).setIndices(indexes: _*).setQuery(QueryBuilders.matchAllQuery())
    def build = _builder.request()

    def routing(routing: String): CountDefinition = {
      _builder.setRouting(routing)
      this
    }
    def minScore(minScore: Double): CountDefinition = {
      _builder.setMinScore(minScore.toFloat)
      this
    }

    def where(string: String): CountDefinition = query(new StringQueryDefinition(string))
    def where(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
    def query(string: String): CountDefinition = query(new StringQueryDefinition(string))
    def query(block: => QueryDefinition): CountDefinition = javaquery(block.builder)
    def javaquery(block: => QueryBuilder): CountDefinition = {
      _builder.setQuery(block)
      this
    }

    def types(iterable: Iterable[String]): CountDefinition = types(iterable.toSeq: _*)
    def types(types: String*): CountDefinition = {
      _builder.setTypes(types: _*)
      this
    }
  }

}