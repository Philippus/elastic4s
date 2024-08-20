package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object SparseVectorFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): SparseVectorField = SparseVectorField(name)

  def build(field: SparseVectorField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
