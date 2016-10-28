package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeRewrite}
import com.sksamuel.elastic4s2.analyzers.Analyzer
import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.index.query.{Operator, QueryBuilders}

case class QueryStringQueryDefinition(query: String)
  extends QueryDefinition
    with DefinitionAttributeRewrite
    with DefinitionAttributeBoost {

  val builder = QueryBuilders.queryStringQuery(query)
  val _builder = builder

  def analyzer(analyzer: String): this.type = {
    builder.analyzer(analyzer)
    this
  }

  def analyzer(analyzer: Analyzer): this.type = {
    builder.analyzer(analyzer.name)
    this
  }

  def defaultOperator(op: String): this.type = {
    op.toUpperCase match {
      case "AND" => builder.defaultOperator(Operator.AND)
      case _ => builder.defaultOperator(Operator.OR)
    }
    this
  }

  def defaultOperator(op: Operator): this.type = {
    builder.defaultOperator(op)
    this
  }

  def asfields(fields: String*): this.type = {
    fields foreach field
    this
  }

  def lowercaseExpandedTerms(lowercaseExpandedTerms: Boolean): this.type = {
    builder.lowercaseExpandedTerms(lowercaseExpandedTerms)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): this.type = {
    builder.fuzzyPrefixLength(fuzzyPrefixLength)
    this
  }

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): this.type = {
    builder.fuzzyMaxExpansions(fuzzyMaxExpansions)
    this
  }

  def fuzzyRewrite(fuzzyRewrite: String): this.type = {
    builder.fuzzyRewrite(fuzzyRewrite)
    this
  }

  def tieBreaker(tieBreaker: Double): this.type = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }

  def allowLeadingWildcard(allowLeadingWildcard: Boolean): this.type = {
    builder.allowLeadingWildcard(allowLeadingWildcard)
    this
  }

  def lenient(lenient: Boolean): this.type = {
    builder.lenient(lenient)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: Int): this.type = {
    builder.minimumShouldMatch(minimumShouldMatch.toString)
    this
  }

  def enablePositionIncrements(enablePositionIncrements: Boolean): this.type = {
    builder.enablePositionIncrements(enablePositionIncrements)
    this
  }

  def quoteFieldSuffix(quoteFieldSuffix: String): this.type = {
    builder.quoteFieldSuffix(quoteFieldSuffix)
    this
  }

  def field(name: String): this.type = {
    builder.field(name)
    this
  }

  def field(name: String, boost: Double): this.type = {
    builder.field(name, boost.toFloat)
    this
  }

  def defaultField(field: String): this.type = {
    builder.defaultField(field)
    this
  }

  def analyzeWildcard(analyzeWildcard: Boolean): this.type = {
    builder.analyzeWildcard(analyzeWildcard)
    this
  }

  def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean): this.type = {
    builder.autoGeneratePhraseQueries(autoGeneratePhraseQueries)
    this
  }

  def operator(op: String): this.type = {
    op.toLowerCase match {
      case "and" => builder.defaultOperator(Operator.AND)
      case _ => builder.defaultOperator(Operator.OR)
    }
    this
  }

  def phraseSlop(phraseSlop: Int): QueryStringQueryDefinition = {
    builder.phraseSlop(phraseSlop)
    this
  }
}
