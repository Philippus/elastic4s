package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.AnnotatedTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AnnotatedTextFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): AnnotatedTextField = AnnotatedTextField(
    name,
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty)
  )

  def build(field: AnnotatedTextField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    builder.endObject()
  }
}
