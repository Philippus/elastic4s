package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.RankFeatureField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RankFeatureFieldBuilderFn {
  def build(field: RankFeatureField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.positiveScoreImpact.foreach(builder.field("positive_score_impact", _))
    builder.endObject()
  }
}
