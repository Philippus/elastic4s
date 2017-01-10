package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DefinitionAttributes._
import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.{Operator, QueryBuilders}
import org.elasticsearch.index.search.MatchQuery

case class MatchQueryDefinition(field: String, value: Any)
  extends QueryDefinition
    with DefinitionAttributeBoost
    with DefinitionAttributeFuzziness
    with DefinitionAttributeFuzzyRewrite
    with DefinitionAttributePrefixLength
    with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.matchQuery(field, value)
  val _builder = builder

  def analyzer(a: Analyzer): MatchQueryDefinition = {
    builder.analyzer(a.name)
    this
  }

  @deprecated("for phrase queries use match phrase query", "5.0.0")
  def slop(s: Int) = {
    builder.slop(s)
    this
  }

  def lenient(lenient: Boolean) = {
    builder.lenient(lenient)
    this
  }

  @deprecated("use lenient(Boolean)", "5.0.0")
  def setLenient(lenient: Boolean) = {
    builder.lenient(lenient)
    this
  }

  def fuzzyTranspositions(f: Boolean): MatchQueryDefinition = {
    builder.fuzzyTranspositions(f)
    this
  }

  def maxExpansions(max: Int) = {
    builder.maxExpansions(max)
    this
  }

  def minimumShouldMatch(a: Any) = {
    builder.minimumShouldMatch(a.toString)
    this
  }

  def withAndOperator() = operator(Operator.AND)
  def withOrOperator() = operator(Operator.AND)

  def operator(op: String): MatchQueryDefinition = {
    op match {
      case "AND" => builder.operator(Operator.AND)
      case _ => builder.operator(Operator.OR)
    }
    this
  }

  def operator(op: Operator) = {
    builder.operator(op)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }

  def zeroTermsQuery(zeroTerms: MatchQuery.ZeroTermsQuery) = {
    builder.zeroTermsQuery(zeroTerms)
    this
  }

}
