package com.sksamuel.elastic4s.searches.queries.span

case class SpanOrQueryDefinition(clauses: Seq[SpanQueryDefinition],
                                 boost: Option[Double] = None,
                                 queryName: Option[String] = None) extends SpanQueryDefinition {

  def boost(boost: Double): SpanOrQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): SpanOrQueryDefinition = withQueryName(queryName)

  def clause(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanOrQueryDefinition = clauses(first +: rest)
  def clauses(clauses: Iterable[SpanQueryDefinition]): SpanOrQueryDefinition = copy(clauses = this.clauses ++ clauses)

  def withQueryName(queryName: String): SpanOrQueryDefinition = copy(queryName = Option(queryName))
}
