package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DenseVectorField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): DenseVectorField = DenseVectorField(
    name,
    values.get("dims").map(_.asInstanceOf[Int]).get
  )


  def build(field: DenseVectorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("dims", field.dims)
    builder.endObject()
  }
}
