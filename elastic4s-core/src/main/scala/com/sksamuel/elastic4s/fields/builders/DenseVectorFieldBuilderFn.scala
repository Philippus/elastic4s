package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.DenseVectorField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DenseVectorFieldBuilderFn {

  def build(field: DenseVectorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("dims", field.dims)
    builder.endObject()
  }
}
