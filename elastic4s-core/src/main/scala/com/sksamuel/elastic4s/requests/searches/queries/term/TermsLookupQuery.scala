package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.TermsLookup
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class TermsLookupQuery(field: String, termsLookup: TermsLookup, queryName: Option[String] = None) extends Query {
  def queryName(name: String): TermsLookupQuery = copy(queryName = name.some)
}


