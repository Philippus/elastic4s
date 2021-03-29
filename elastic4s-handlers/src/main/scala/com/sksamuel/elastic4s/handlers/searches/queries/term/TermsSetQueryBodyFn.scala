package com.sksamuel.elastic4s.handlers.searches.queries.term

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.term.TermsSetQuery

object TermsSetQueryBodyFn {
  def apply(t: TermsSetQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("terms_set")
    builder.startObject(t.field)
    builder.array("terms", t.terms.toArray[String])
    t.minimumShouldMatchField.foreach(builder.field("minimum_should_match_field", _))
    t.minimumShouldMatchScript.foreach(script => builder.rawField("minimum_should_match_script", handlers.script.ScriptBuilderFn(script)))
    builder.endObject().endObject()
  }
}
