package com.sksamuel.elastic4s.handlers.synonyms

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.synonyms.CreateOrUpdateSynonymRuleRequest

object UpdateSynonymRuleBodyFn {
  def apply(request: CreateOrUpdateSynonymRuleRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("synonyms", request.synonyms)
  }
}
