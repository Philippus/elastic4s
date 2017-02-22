package com.sksamuel.elastic4s.searches.queries.term

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.indices.TermsLookup
import com.sksamuel.exts.OptionImplicits._

case class TermsLookupQueryDefinition(field: String,
                                      termsLookup: TermsLookup,
                                      queryName: Option[String] = None) extends QueryDefinition {
  def queryName(name: String): TermsLookupQueryDefinition = copy(queryName = name.some)
}
