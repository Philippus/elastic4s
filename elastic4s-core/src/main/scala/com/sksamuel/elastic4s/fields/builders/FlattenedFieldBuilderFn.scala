package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.FlattenedField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object FlattenedFieldBuilderFn {
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
