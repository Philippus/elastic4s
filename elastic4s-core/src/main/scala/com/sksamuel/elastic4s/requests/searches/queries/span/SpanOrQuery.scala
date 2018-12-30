package com.sksamuel.elastic4s.requests.searches.queries.span

case class SpanOrQuery(clauses: Seq[SpanQuery], boost: Option[Double] = None, queryName: Option[String] = None)
    extends SpanQuery {

  def boost(boost: Double): SpanOrQuery         = copy(boost = Option(boost))
  def queryName(queryName: String): SpanOrQuery = withQueryName(queryName)

  def clause(first: SpanQuery, rest: SpanQuery*): SpanOrQuery = clauses(first +: rest)
  def clauses(clauses: Iterable[SpanQuery]): SpanOrQuery      = copy(clauses = this.clauses ++ clauses)

  def withQueryName(queryName: String): SpanOrQuery = copy(queryName = Option(queryName))
}
