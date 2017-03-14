package com.sksamuel.elastic4s.searches.queries.span

case class SpanNearQueryDefinition(clauses: Seq[SpanQueryDefinition],
                                   slop: Int,
                                   boost: Option[Double] = None,
                                   inOrder: Option[Boolean] = None,
                                   queryName: Option[String] = None) extends SpanQueryDefinition {


  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanNearQueryDefinition = clauses(first +: rest)
  def clauses(clauses: Iterable[SpanQueryDefinition]): SpanNearQueryDefinition = copy(clauses = this.clauses ++ clauses)

  def boost(boost: Double): SpanNearQueryDefinition = copy(boost = Option(boost))
  def queryName(name: String): SpanNearQueryDefinition = copy(queryName = Option(name))
  def inOrder(inOrder: Boolean): SpanNearQueryDefinition = copy(inOrder = Option(inOrder))
}
