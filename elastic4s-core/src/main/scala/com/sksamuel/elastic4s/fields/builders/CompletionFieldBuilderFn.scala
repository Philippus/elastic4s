package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.CompletionField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object CompletionFieldBuilderFn {

  def build(field: CompletionField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))

    if (field.copyTo.nonEmpty)
      builder.array("copy_to", field.copyTo.toArray)

    field.index.foreach(builder.field("index", _))

    field.preservePositionIncrements.foreach(builder.field("preserve_position_increments", _))
    field.preserveSeparators.foreach(builder.field("preserve_separators", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.maxInputLength.foreach(builder.field("max_input_length", _))
    if (field.contexts.nonEmpty) {
      builder.startArray("contexts")
      field.contexts.foreach { context =>
        builder.startObject()
        builder.field("name", context.name)
        builder.field("type", context.`type`)
        context.path.foreach(builder.field("path", _))
        if (context.`type` == "geo") context.precision.foreach(builder.field("precision", _))
        builder.endObject()
      }
      builder.endArray()
    }

    field.norms.foreach(builder.field("norms", _))
    field.store.foreach(builder.field("store", _))

    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.termVector.foreach(builder.field("term_vector", _))

    builder.endObject()
  }
}
