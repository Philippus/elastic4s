package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.RankVectorsField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RankVectorsFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): RankVectorsField =
    RankVectorsField(
      name,
      values.get("element_type").map(_.asInstanceOf[String]),
      values.get("dims").map(_.asInstanceOf[Int])
    )

  def build(field: RankVectorsField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.elementType.foreach(builder.field("element_type", _))
    field.dims.foreach(dims => builder.field("dims", dims))
    builder.endObject()
  }
}
