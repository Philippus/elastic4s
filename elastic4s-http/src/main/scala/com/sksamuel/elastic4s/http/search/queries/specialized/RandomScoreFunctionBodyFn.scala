package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.RandomScoreFunctionDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object RandomScoreFunctionBodyFn {
  def apply(random: RandomScoreFunctionDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.field("seed", random.seed)
    builder.endObject()
    builder
  }
}
