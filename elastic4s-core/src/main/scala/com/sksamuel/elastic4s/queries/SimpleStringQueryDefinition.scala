package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.index.query.{QueryBuilders, SimpleQueryStringBuilder, SimpleQueryStringFlag}

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
      case "AND" => builder.defaultOperator(SimpleQueryStringBuilder.Operator.AND)
      case _ => builder.defaultOperator(SimpleQueryStringBuilder.Operator.OR)
    }
    this
  }

  def defaultOperator(d: SimpleQueryStringBuilder.Operator): SimpleStringQueryDefinition = {
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
