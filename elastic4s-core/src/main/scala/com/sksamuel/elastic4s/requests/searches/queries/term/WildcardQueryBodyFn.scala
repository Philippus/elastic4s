package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.term.WildcardQuery

object WildcardQueryBodyFn {
  def apply(q: WildcardQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("wildcard").startObject(q.field)
    builder.autofield("value", q.query)
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    q.caseInsensitive.foreach(builder.field("case_insensitive",_))
    builder.endObject().endObject().endObject()
  }
}
