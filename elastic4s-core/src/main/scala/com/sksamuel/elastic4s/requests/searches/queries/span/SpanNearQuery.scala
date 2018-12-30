package com.sksamuel.elastic4s.requests.searches.queries.span

case class SpanNearQuery(clauses: Seq[SpanQuery],
                         slop: Int,
                         boost: Option[Double] = None,
                         inOrder: Option[Boolean] = None,
                         queryName: Option[String] = None)
    extends SpanQuery {

  def clause(first: SpanQuery, rest: SpanQuery*): SpanNearQuery = clauses(first +: rest)
  def clauses(clauses: Iterable[SpanQuery]): SpanNearQuery      = copy(clauses = this.clauses ++ clauses)

  def boost(boost: Double): SpanNearQuery      = copy(boost = Option(boost))
  def queryName(name: String): SpanNearQuery   = copy(queryName = Option(name))
  def inOrder(inOrder: Boolean): SpanNearQuery = copy(inOrder = Option(inOrder))
}
