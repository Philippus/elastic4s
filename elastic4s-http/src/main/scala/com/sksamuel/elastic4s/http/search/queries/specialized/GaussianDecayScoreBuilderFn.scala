package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.funcscorer.GaussianDecayScoreDefinition

object GaussianDecayScoreBuilderFn {
  def apply(g: GaussianDecayScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("gaussian")
    builder.startObject(g.field)
    builder.field("origin", g.origin)
    builder.field("scale", g.scale)
    g.offset.map(_.toString).foreach(builder.field("offset", _))
    g.decay.foreach(builder.field("decay", _))
    g.weight.foreach(builder.field("weight", _))
    builder.endObject().endObject()
  }
}
