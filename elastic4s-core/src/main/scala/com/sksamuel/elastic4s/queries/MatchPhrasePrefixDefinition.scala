package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeCutoffFrequency, DefinitionAttributeFuzziness, DefinitionAttributeFuzzyRewrite, DefinitionAttributePrefixLength}
import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.{Operator, QueryBuilders}
import org.elasticsearch.index.search.MatchQuery

case class MatchPhrasePrefixDefinition(field: String, value: Any)
  extends QueryDefinition
    with DefinitionAttributeBoost
    with DefinitionAttributeFuzziness
    with DefinitionAttributeFuzzyRewrite
    with DefinitionAttributePrefixLength
    with DefinitionAttributeCutoffFrequency {

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

  def zeroTermsQuery(z: MatchQuery.ZeroTermsQuery): MatchPhrasePrefixDefinition = {
    builder.zeroTermsQuery(z)
    this
  }

  def slop(s: Int): MatchPhrasePrefixDefinition = {
    builder.slop(s)
    this
  }

  def operator(op: Operator): MatchPhrasePrefixDefinition = {
    builder.operator(op)
    this
  }

  def operator(op: String): MatchPhrasePrefixDefinition = {
    op match {
      case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
      case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
    }
    this
  }

  def minimumShouldMatch(a: Any): MatchPhrasePrefixDefinition = {
    builder.minimumShouldMatch(a.toString)
    this
  }

  def maxExpansions(max: Int): MatchPhrasePrefixDefinition = {
    builder.maxExpansions(max)
    this
  }

  def fuzzyTranspositions(f: Boolean): MatchPhrasePrefixDefinition = {
    builder.fuzzyTranspositions(f)
    this
  }
}
