package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.RankFeaturesField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RankFeaturesFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): RankFeaturesField = RankFeaturesField(name)

  def build(field: RankFeaturesField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
