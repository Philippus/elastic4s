package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.ExponentialDecayScoreDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ExponentialDecayScoreBodyFn {
  def apply(exp: ExponentialDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.startObject(exp.field)
    builder.field("origin", exp.origin)
    builder.field("scale", exp.scale)
    exp.offset.map(o => builder.field("offset", o))
    exp.decay.map(d => builder.field("decay", d))
    builder.endObject()

    exp.multiValueMode.map(mvm => builder.field("multi_value_mode", mvm.toString.toLowerCase))

    builder.endObject()
    builder
  }
}
