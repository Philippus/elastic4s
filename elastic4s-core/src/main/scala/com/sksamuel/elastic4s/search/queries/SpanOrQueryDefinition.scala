package com.sksamuel.elastic4s.search.queries

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

  def boost(boost: Double): SpanOrQueryDefinition =
    copy(boost = Option(boost))

  def clauses(clauses: Iterable[SpanQueryDefinition]): SpanOrQueryDefinition =
    copy(clauses = this.clauses ++ clauses)

  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanOrQueryDefinition =
    clauses(first +: rest)

  def withQueryName(queryName: String): SpanOrQueryDefinition =
    copy(queryName = Option(queryName))
}
