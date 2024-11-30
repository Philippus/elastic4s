package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{CompletionField, ContextField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object CompletionFieldBuilderFn {
  private def toContextField(values: Map[String, Any]) = ContextField(
    values.get("name").map(_.asInstanceOf[String]).get,
    values.get("type").map(_.asInstanceOf[String]).get,
    values.get("path").map(_.asInstanceOf[String]),
    values.get("precision").map(_.asInstanceOf[Int])
  )

  def toField(name: String, values: Map[String, Any]): CompletionField = CompletionField(
    name,
    values.get("analyzer").map(_.asInstanceOf[String]),
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("ignore_above").map(_.asInstanceOf[Int]),
    values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
    values.get("max_input_length").map(_.asInstanceOf[Int]),
    values.get("norms").map(_.asInstanceOf[Boolean]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("preserve_separators").map(_.asInstanceOf[Boolean]),
    values.get("preserve_position_increments").map(_.asInstanceOf[Boolean]),
    values.get("similarity").map(_.asInstanceOf[String]),
    values.get("search_analyzer").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean]),
    values.get("term_vector").map(_.asInstanceOf[String]),
    values.get("contexts").map(_.asInstanceOf[Seq[Map[String, Any]]]).map(_.map(toContextField)).getOrElse(Seq.empty)
  )

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
