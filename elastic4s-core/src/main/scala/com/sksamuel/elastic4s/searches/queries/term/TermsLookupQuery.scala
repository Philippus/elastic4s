package com.sksamuel.elastic4s.searches.queries.term

import com.sksamuel.elastic4s.script.Script
import com.sksamuel.elastic4s.searches.TermsLookup
import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class TermsLookupQuery(field: String, termsLookup: TermsLookup, queryName: Option[String] = None)
    extends Query {
  def queryName(name: String): TermsLookupQuery = copy(queryName = name.some)
}

case class TermsSetQuery(field: String,
                         terms: Set[Any],
                         minimumShouldMatchField: Option[Int] = None,
                         minimumShouldMatchScript: Option[Script] = None,
                         queryName: Option[String] = None) {
  require(terms.nonEmpty)
  def queryName(name: String): TermsSetQuery                            = copy(queryName = name.some)
  def minimumShouldMatchField(field: Int): TermsSetQuery                = copy(minimumShouldMatchField = field.some)
  def minimumShouldMatchScript(script: Script): TermsSetQuery = copy(minimumShouldMatchScript = script.some)

}
