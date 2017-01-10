package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeCutoffFrequency, DefinitionAttributeFuzziness, DefinitionAttributeFuzzyRewrite, DefinitionAttributePrefixLength}
import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.{MultiMatchQueryBuilder, Operator, QueryBuilders}
import org.elasticsearch.index.search.MatchQuery

import scala.collection.JavaConverters._

case class MultiMatchQueryDefinition(text: String)
  extends QueryDefinition
    with DefinitionAttributeFuzziness
    with DefinitionAttributePrefixLength
    with DefinitionAttributeFuzzyRewrite
    with DefinitionAttributeCutoffFrequency {

  val _builder = QueryBuilders.multiMatchQuery(text)
  val builder = _builder

  def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.name)

  def analyzer(a: String): MultiMatchQueryDefinition = {
    builder.analyzer(a)
    this
  }

  def boost(boost: Double): MultiMatchQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def fields(_fields: Iterable[String]) = {
    for (f <- _fields) builder.field(f)
    this
  }

  def field(name: String, boost: Float): MultiMatchQueryDefinition = {
    builder.field(name, boost)
    this
  }

  def fields(_fields: String*): MultiMatchQueryDefinition = fields(_fields.toIterable)

  def lenient(l: Boolean): MultiMatchQueryDefinition = {
    builder.lenient(l)
    this
  }

  def maxExpansions(maxExpansions: Int): MultiMatchQueryDefinition = {
    builder.maxExpansions(maxExpansions)
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

  def slop(slop: Int): MultiMatchQueryDefinition = {
    builder.slop(slop)
    this
  }

  def fields(fields: Map[String, Float]): MultiMatchQueryDefinition = {
    builder.fields(fields.map { case (s, f) => s -> java.lang.Float.valueOf(f) }.asJava)
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

  def matchType(t: MultiMatchQueryBuilder.Type): MultiMatchQueryDefinition = {
    builder.`type`(t)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }

  def tieBreaker(tieBreaker: Double): MultiMatchQueryDefinition = {
    builder.tieBreaker(java.lang.Float.valueOf(tieBreaker.toFloat))
    this
  }

  @deprecated("@deprecated use a tieBreaker of 1.0f to disable dis-max query or select the appropriate Type", "1.2.0")
  def useDisMax(useDisMax: Boolean): MultiMatchQueryDefinition = {
    builder.useDisMax(java.lang.Boolean.valueOf(useDisMax))
    this
  }

  def zeroTermsQuery(q: MatchQuery.ZeroTermsQuery): MultiMatchQueryDefinition = {
    builder.zeroTermsQuery(q)
    this
  }
}
