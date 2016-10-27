package com.sksamuel.elastic4s.query

import org.elasticsearch.index.query.{QueryBuilders, SpanOrQueryBuilder}

case class SpanOrQueryDefinition(clauses: Seq[SpanQueryDefinition],
                                 boost: Option[Double] = None,
                                 queryName: Option[String] = None) extends SpanQueryDefinition {

  def builder: SpanOrQueryBuilder = {

    val initial = clauses.headOption.getOrElse(sys.error("Must have at least one clause"))
    val builder = QueryBuilders.spanOrQuery(initial.builder)
    clauses.tail.map(_.builder).foreach(builder.addClause)
    boost.map(_.toFloat).foreach(builder.boost)
    queryName.foreach(builder.queryName)
    builder
  }

  def boost(boost: Double) = copy(boost = Option(boost))
  def clauses(clauses: Iterable[SpanQueryDefinition]) = copy(clauses = this.clauses ++ clauses)
  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*) = clauses(first +: rest)
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
