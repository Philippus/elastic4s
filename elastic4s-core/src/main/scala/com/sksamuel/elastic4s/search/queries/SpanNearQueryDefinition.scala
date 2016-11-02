package com.sksamuel.elastic4s.search.queries

import org.elasticsearch.index.query.{QueryBuilders, SpanNearQueryBuilder}

case class SpanNearQueryDefinition(clauses: Seq[SpanQueryDefinition],
                                   slop: Int,
                                   boost: Option[Double] = None,
                                   inOrder: Option[Boolean] = None,
                                   queryName: Option[String] = None) extends SpanQueryDefinition {

  def builder: SpanNearQueryBuilder = {
    val initial = clauses.headOption.getOrElse(sys.error("Must have at least one clause"))
    val builder = QueryBuilders.spanNearQuery(initial.builder, slop)
    clauses.tail.map(_.builder).foreach(builder.addClause)
    boost.map(_.toFloat).foreach(builder.boost)
    queryName.foreach(builder.queryName)
    inOrder.foreach(builder.inOrder)
    builder
  }

  def boost(boost: Double): SpanNearQueryDefinition =
    copy(boost = Option(boost))

  def clauses(clauses: Iterable[SpanQueryDefinition]): SpanNearQueryDefinition =
    copy(clauses = this.clauses ++ clauses)

  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanNearQueryDefinition =
    clauses(first +: rest)

  def inOrder(inOrder: Boolean): SpanNearQueryDefinition =
    copy(inOrder = Option(inOrder))
}
