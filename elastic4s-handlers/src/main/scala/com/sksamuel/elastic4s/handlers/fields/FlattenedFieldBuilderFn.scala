package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.FlattenedField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object FlattenedFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): FlattenedField = FlattenedField(
    name,
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("depth_limit").map(_.asInstanceOf[Int]),
    values.get("eager_global_ordinals").map(_.asInstanceOf[Boolean]),
    values.get("ignore_above").map(_.asInstanceOf[Int]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("similarity").map(_.asInstanceOf[String]),
    values.get("split_queries_on_whitespace").map(_.asInstanceOf[Boolean])
  )

  def build(field: FlattenedField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.boost.foreach(builder.field("boost", _))
    field.depthLimit.foreach(builder.field("depth_limit", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.index.foreach(builder.field("index", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.splitQueriesOnWhitespace.foreach(builder.field("split_queries_on_whitespace", _))

    builder.endObject()
  }
}
