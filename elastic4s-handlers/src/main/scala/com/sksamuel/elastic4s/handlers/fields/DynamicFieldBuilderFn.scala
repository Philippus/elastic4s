package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DynamicField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DynamicFieldBuilderFn {

  def build(field: DynamicField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))
    field.coerce.foreach(builder.field("coerce", _))
    if (field.copyTo.nonEmpty)
      builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.enabled.foreach(builder.field("enabled", _))
    field.fielddata.foreach(builder.field("fielddata", _))
    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }
    field.format.foreach(builder.field("format", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.index.foreach(builder.field("index", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.locale.foreach(builder.field("locale", _))
    field.norms.foreach(builder.field("norms", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.scalingFactor.foreach(builder.field("scaling_factor", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.store.foreach(builder.field("store", _))
    field.termVector.foreach(builder.field("term_vector", _))

    builder.endObject()
  }
}
