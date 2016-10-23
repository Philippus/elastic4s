package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeCutoffFrequency, DefinitionAttributeFuzziness, DefinitionAttributeFuzzyRewrite, DefinitionAttributePrefixLength}
import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.{MultiMatchQueryBuilder, Operator, QueryBuilders}
import org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery

case class MultiMatchQueryDefinition(text: String)
  extends QueryDefinition
    with DefinitionAttributeFuzziness
    with DefinitionAttributePrefixLength
    with DefinitionAttributeFuzzyRewrite
    with DefinitionAttributeCutoffFrequency {

  val _builder = QueryBuilders.multiMatchQuery(text)
  val builder = _builder

  def maxExpansions(maxExpansions: Int): MultiMatchQueryDefinition = {
    builder.maxExpansions(maxExpansions)
    this
  }

  def fields(_fields: Iterable[String]) = {
    for ( f <- _fields ) builder.field(f)
    this
  }

  def fields(_fields: String*): MultiMatchQueryDefinition = fields(_fields.toIterable)

  def boost(boost: Double): MultiMatchQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.name)

  def analyzer(a: String): MultiMatchQueryDefinition = {
    builder.analyzer(a)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: Int): MultiMatchQueryDefinition = {
    builder.minimumShouldMatch(minimumShouldMatch.toString)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: String): MultiMatchQueryDefinition = {
    builder.minimumShouldMatch(minimumShouldMatch: String)
    this
  }

  def lenient(l: Boolean): MultiMatchQueryDefinition = {
    builder.lenient(l)
    this
  }

  def zeroTermsQuery(q: ZeroTermsQuery): MultiMatchQueryDefinition = {
    builder.zeroTermsQuery(q)
    this
  }

  def tieBreaker(tieBreaker: Double): MultiMatchQueryDefinition = {
    builder.tieBreaker(java.lang.Float.valueOf(tieBreaker.toFloat))
    this
  }

  def operator(op: Operator): MultiMatchQueryDefinition = {
    builder.operator(op)
    this
  }

  def operator(op: String): MultiMatchQueryDefinition = {
    op match {
      case "AND" => builder.operator(Operator.AND)
      case _ => builder.operator(Operator.OR)
    }
    this
  }

  def matchType(t: MultiMatchQueryBuilder.Type): MultiMatchQueryDefinition = {
    builder.`type`(t)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }

  def matchType(t: String): MultiMatchQueryDefinition = {
    val mt = t match {
      case "most_fields" => MultiMatchQueryBuilder.Type.MOST_FIELDS
      case "cross_fields" => MultiMatchQueryBuilder.Type.CROSS_FIELDS
      case "phrase" => MultiMatchQueryBuilder.Type.PHRASE
      case "phrase_prefix" => MultiMatchQueryBuilder.Type.PHRASE_PREFIX
      case _ => MultiMatchQueryBuilder.Type.BEST_FIELDS
    }

    matchType(mt)
  }
}
