package com.sksamuel.elastic4s.searches.queries.term

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class TermsQueryDefinition[T](field: String,
                                   values: Iterable[T],
                                   boost: Option[Double] = None,
                                   queryName: Option[String] = None)
                                  (implicit val buildable: BuildableTermsQuery[T]) extends QueryDefinition {

  def boost(boost: Double): TermsQueryDefinition[T] = copy(boost = boost.some)
  def queryName(queryName: String): TermsQueryDefinition[T] = copy(queryName = queryName.some)
}

trait BuildableTermsQuery[T] {
  def build(q: TermsQueryDefinition[T]): Any
}
