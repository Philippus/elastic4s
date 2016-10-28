package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes._
import com.sksamuel.elastic4s2.analyzers.Analyzer
import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class MatchPhrasePrefixDefinition(field: String, value: Any)
  extends QueryDefinition
    with DefinitionAttributeBoost {

  def builder = _builder
  val _builder = QueryBuilders.matchPhrasePrefixQuery(field, value.toString)

  def analyzer(a: Analyzer): MatchPhrasePrefixDefinition = {
    builder.analyzer(a.name)
    this
  }

  def analyzer(name: String): MatchPhrasePrefixDefinition = {
    builder.analyzer(name)
    this
  }

  def slop(s: Int): MatchPhrasePrefixDefinition = {
    builder.slop(s)
    this
  }

  def maxExpansions(max: Int): MatchPhrasePrefixDefinition = {
    builder.maxExpansions(max)
    this
  }
}
