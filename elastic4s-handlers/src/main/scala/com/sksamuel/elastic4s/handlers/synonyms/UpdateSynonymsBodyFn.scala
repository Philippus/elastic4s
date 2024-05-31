package com.sksamuel.elastic4s.handlers.synonyms

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.synonyms.CreateOrUpdateSynonymsSetRequest

object UpdateSynonymsBodyFn {
  def apply(request: CreateOrUpdateSynonymsSetRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startArray("synonyms_set")
    request.synonymRules.foreach { rule =>
      builder.startObject()
      rule.id.foreach(builder.field("id", _))
      builder.field("synonyms", rule.synonyms)
      builder.endObject()
    }
    builder.endArray()
  }
}
