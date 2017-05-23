package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.GaussianDecayScoreDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object GaussianDecayScoreBodyFn {
  def apply(g: GaussianDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(o => builder.field("offset", o))
    g.decay.map(d => builder.field("decay", d))
    builder.endObject()

    g.multiValueMode.map(mvm => builder.field("multi_value_mode", mvm.toString.toLowerCase))

    builder.endObject()
    builder
  }
}
