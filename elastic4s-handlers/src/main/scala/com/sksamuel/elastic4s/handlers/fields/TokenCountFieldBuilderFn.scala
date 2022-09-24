package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.TokenCountField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object TokenCountFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): TokenCountField = TokenCountField(
    name,
    values.get("analyzer").map(_.asInstanceOf[String]),
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("enable_position_increments").map(_.asInstanceOf[Boolean]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean])
  )


  def build(field: TokenCountField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.enablePositionIncrements.foreach(builder.field("enable_position_increments", _))
    field.index.foreach(builder.field("index", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))

    builder.endObject()
  }
}
