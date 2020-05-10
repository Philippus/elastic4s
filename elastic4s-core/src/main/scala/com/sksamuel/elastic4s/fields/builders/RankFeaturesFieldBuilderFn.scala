package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.RankFeaturesField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RankFeaturesFieldBuilderFn {
  def build(field: RankFeaturesField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
