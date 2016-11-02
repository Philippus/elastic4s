package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeCutoffFrequency}
import com.sksamuel.elastic4s2.analyzers.Analyzer
import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.index.query.{Operator, QueryBuilders}

case class CommonTermsQueryDefinition(name: String, text: String)
  extends QueryDefinition
    with DefinitionAttributeBoost
    with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.commonTermsQuery(name, text)
  val _builder = builder

  def queryName(queryName: String): CommonTermsQueryDefinition = {
    builder.queryName(queryName)
    this
  }

  def highFreqMinimumShouldMatch(highFreqMinimumShouldMatch: Int): CommonTermsQueryDefinition = {
    builder.highFreqMinimumShouldMatch(highFreqMinimumShouldMatch.toString)
    this
  }

  def highFreqOperator(operator: String): CommonTermsQueryDefinition = {
    builder.highFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
    this
  }

  def analyzer(analyzer: Analyzer): CommonTermsQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }

  def lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch: Int): CommonTermsQueryDefinition = {
    builder.lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch.toString)
    this
  }

  def lowFreqOperator(operator: String): CommonTermsQueryDefinition = {
    builder.lowFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
    this
  }
}
