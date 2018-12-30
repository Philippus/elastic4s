package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.PrefixQuery
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object PrefixQueryBodyFn {
  def apply(q: PrefixQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("prefix")

    builder.startObject(q.field)
    builder.autofield("value", q.prefix)
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject().endObject()
  }
}
