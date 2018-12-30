package com.sksamuel.elastic4s.requests.searches.queries

case class IdQuery(ids: Seq[Any],
                   types: Seq[String] = Nil,
                   boost: Option[Double] = None,
                   queryName: Option[String] = None)
    extends Query {

  def types(types: Iterable[String]): IdQuery      = copy(types = types.toSeq)
  def types(first: String, rest: String*): IdQuery = copy(types = first +: rest)

  def queryName(name: String): IdQuery = copy(queryName = Option(name))
  def boost(boost: Double): IdQuery    = copy(boost = Option(boost))
}
