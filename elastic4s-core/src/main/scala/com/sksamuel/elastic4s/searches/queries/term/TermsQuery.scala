package com.sksamuel.elastic4s.searches.queries.term

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class TermsQuery[T](field: String,
                         values: Iterable[T],
                         boost: Option[Double] = None,
                         ref: Option[DocumentRef] = None,
                         routing: Option[String] = None,
                         path: Option[String] = None,
                         queryName: Option[String] = None)(implicit val buildable: BuildableTermsQuery[T])
    extends Query {

  def ref(index: String, `type`: String, id: String): TermsQuery[T] = ref(DocumentRef(index, `type`, id))
  def ref(ref: DocumentRef): TermsQuery[T]                          = copy(ref = ref.some)
  def routing(routing: String): TermsQuery[T]                       = copy(routing = routing.some)
  def path(path: String): TermsQuery[T]                             = copy(path = path.some)
  def boost(boost: Double): TermsQuery[T]                           = copy(boost = boost.some)
  def queryName(queryName: String): TermsQuery[T]                   = copy(queryName = queryName.some)
}

trait BuildableTermsQuery[T] {
  def build(q: TermsQuery[T]): Any
}
