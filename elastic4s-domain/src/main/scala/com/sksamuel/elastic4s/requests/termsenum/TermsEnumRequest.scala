package com.sksamuel.elastic4s.requests.termsenum

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class TermsEnumRequest(indexes: Indexes,
                            field: String,
                            string: Option[String] = None,
                            size: Option[Int] = None,
                            timeout: Option[String] = None,
                            caseInsensitive: Option[Boolean] = None,
                            indexFilter: Option[Query] = None,
                            searchAfter: Option[String] = None
                           ) extends Serializable {
  def string(string: String): TermsEnumRequest = copy(string = string.some)
  def size(size: Int): TermsEnumRequest = copy(size = size.some)
  def timeout(timeout: String): TermsEnumRequest = copy(timeout = timeout.some)
  def caseInsensitive(caseInsensitive: Boolean): TermsEnumRequest = copy(caseInsensitive = caseInsensitive.some)
  def indexFilter(indexFilter: Query): TermsEnumRequest = copy(indexFilter = indexFilter.some)
  def searchAfter(searchAfter: String): TermsEnumRequest = copy(searchAfter = searchAfter.some)
}
