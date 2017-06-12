package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.LinearDecayScoreDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object LinearDecayScoreBodyFn {
  def apply(l: LinearDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.startObject(l.field)
    builder.field("origin", l.origin)
    builder.field("scale", l.scale)
    l.offset.map(o => builder.field("offset", o))
    l.decay.map(d => builder.field("decay", d))
    builder.endObject()

    l.multiValueMode.map(mvm => builder.field("multi_value_mode", mvm.toString.toLowerCase))

    builder.endObject()
    builder
  }
}
