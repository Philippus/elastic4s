package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.{Operator, QueryBuilders, SimpleQueryStringBuilder, SimpleQueryStringFlag}

case class SimpleStringQueryDefinition(query: String) extends QueryDefinition {

  val builder = QueryBuilders.simpleQueryStringQuery(query)

  def analyzer(analyzer: String): SimpleStringQueryDefinition = {
    builder.analyzer(analyzer)
    this
  }

  def analyzer(analyzer: Analyzer): SimpleStringQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }

  def queryName(queryName: String): SimpleStringQueryDefinition = {
    builder.queryName(queryName)
    this
  }

  def defaultOperator(op: String): SimpleStringQueryDefinition = {
    op match {
      case "AND" => builder.defaultOperator(Operator.AND)
      case _ => builder.defaultOperator(Operator.OR)
    }
    this
  }

  def lenient(lenient: Boolean): SimpleStringQueryDefinition = {
    builder.lenient(lenient)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: Int): SimpleStringQueryDefinition = {
    builder.minimumShouldMatch(minimumShouldMatch.toString)
    this
  }

  def analyzeWildcard(analyzeWildcard: Boolean): SimpleStringQueryDefinition = {
    builder.analyzeWildcard(analyzeWildcard)
    this
  }

  def defaultOperator(d: Operator): SimpleStringQueryDefinition = {
    builder.defaultOperator(d)
    this
  }

  def asfields(fields: String*): SimpleStringQueryDefinition = {
    fields foreach field
    this
  }

  def field(name: String): SimpleStringQueryDefinition = {
    builder.field(name)
    this
  }

  def field(name: String, boost: Double): SimpleStringQueryDefinition = {
    builder.field(name, boost.toFloat)
    this
  }

  def flags(flags: SimpleQueryStringFlag*): SimpleStringQueryDefinition = {
    builder.flags(flags: _*)
    this
  }
}
