package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.SpanQueryDefinition
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

  def boost(boost: Double) = copy(boost = Option(boost))
  def clauses(clauses: Iterable[SpanQueryDefinition]) = copy(clauses = this.clauses ++ clauses)
  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*) = clauses(first +: rest)
  def inOrder(inOrder: Boolean) = copy(inOrder = Option(inOrder))
}
