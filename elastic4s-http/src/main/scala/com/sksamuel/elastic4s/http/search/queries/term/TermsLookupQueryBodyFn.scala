package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.term.TermsLookupQueryDefinition

object TermsLookupQueryBodyFn {
  def apply(t: TermsLookupQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    builder.startObject(t.field)
    builder.field("index", t.termsLookup.ref.index)
    builder.field("type", t.termsLookup.ref.`type`)
    builder.field("id", t.termsLookup.ref.id)

    builder.field("path", t.termsLookup.path)
    t.termsLookup.routing.foreach(builder.field("routing", _))
    builder.endObject()

    t.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }
}
