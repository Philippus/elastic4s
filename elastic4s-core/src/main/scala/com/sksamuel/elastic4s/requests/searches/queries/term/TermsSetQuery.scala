package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class TermsSetQuery(field: String,
                         terms: Set[String],
                         minimumShouldMatchField: Option[String] = None,
                         minimumShouldMatchScript: Option[Script] = None,
                         routing: Option[String] = None,
                         queryName: Option[String] = None) extends Query {
  require(terms.nonEmpty, "The list of terms cannot be empty")
  // ElasticSearch needs one of the two 'minimumShouldMatch' parameters to be specified
  require(
    (minimumShouldMatchField.isDefined && minimumShouldMatchScript.isEmpty) || (minimumShouldMatchScript.isDefined && minimumShouldMatchField.isEmpty),
    "Either only minimumShouldMatchField or only minimumShouldMatchScript must be specified"
  )
  def queryName(name: String): TermsSetQuery = copy(queryName = name.some)
  def routing(routing: String): TermsSetQuery = copy(routing = routing.some)
  def minimumShouldMatchField(field: String): TermsSetQuery = copy(minimumShouldMatchField = field.some)
  def minimumShouldMatchScript(script: Script): TermsSetQuery = copy(minimumShouldMatchScript = script.some)
}
