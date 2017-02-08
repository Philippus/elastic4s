package com.sksamuel.elastic4s.searches.queries.term

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class TermsQueryDefinition[T](field: String,
                                   values: Iterable[T],
                                   boost: Option[Double] = None,
                                   ref: Option[DocumentRef] = None,
                                   routing: Option[String] = None,
                                   path: Option[String] = None,
                                   queryName: Option[String] = None)
                                  (implicit val buildable: BuildableTermsQuery[T]) extends QueryDefinition {

  def ref(index: String, `type`: String, id: String): TermsQueryDefinition[T] = ref(DocumentRef(index, `type`, id))
  def ref(ref: DocumentRef): TermsQueryDefinition[T] = copy(ref = ref.some)
  def routing(routing: String): TermsQueryDefinition[T] = copy(routing = routing.some)
  def path(path: String): TermsQueryDefinition[T] = copy(path = path.some)
  def boost(boost: Double): TermsQueryDefinition[T] = copy(boost = boost.some)
  def queryName(queryName: String): TermsQueryDefinition[T] = copy(queryName = queryName.some)
}

trait BuildableTermsQuery[T] {
  def build(q: TermsQueryDefinition[T]): Any
}
