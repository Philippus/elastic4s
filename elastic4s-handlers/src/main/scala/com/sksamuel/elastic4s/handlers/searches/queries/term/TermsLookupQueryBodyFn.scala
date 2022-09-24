package com.sksamuel.elastic4s.handlers.searches.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.term.TermsLookupQuery

object TermsLookupQueryBodyFn {
  def apply(t: TermsLookupQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    builder.startObject(t.field)
    builder.field("index", t.termsLookup.ref.index.name)
    builder.field("id", t.termsLookup.ref.id)

    builder.field("path", t.termsLookup.path)
    t.termsLookup.routing.foreach(builder.field("routing", _))
    builder.endObject()

    t.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }
}
