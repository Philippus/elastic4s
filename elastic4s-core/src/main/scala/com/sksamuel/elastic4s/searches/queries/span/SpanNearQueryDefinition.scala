package com.sksamuel.elastic4s.searches.queries.span

case class SpanNearQueryDefinition(clauses: Seq[SpanQueryDefinition],
                                   slop: Int,
                                   boost: Option[Double] = None,
                                   inOrder: Option[Boolean] = None,
                                   queryName: Option[String] = None) extends SpanQueryDefinition {

  def boost(boost: Double): SpanNearQueryDefinition =
    copy(boost = Option(boost))

  def clauses(clauses: Iterable[SpanQueryDefinition]): SpanNearQueryDefinition =
    copy(clauses = this.clauses ++ clauses)

  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanNearQueryDefinition =
    clauses(first +: rest)

  def inOrder(inOrder: Boolean): SpanNearQueryDefinition =
    copy(inOrder = Option(inOrder))
}
