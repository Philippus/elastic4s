package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.KeywordField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object KeywordFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): KeywordField = KeywordField(
    name,
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("eager_global_ordinals").map(_.asInstanceOf[Boolean]),
    values
      .get("fields")
      .map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
        ElasticFieldBuilderFn.construct(key, value)
      }.toList).getOrElse(List.empty),
    values.get("ignore_above").map(_.asInstanceOf[Int]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("norms").map(_.asInstanceOf[Boolean]),
    values.get("normalizer").map(_.asInstanceOf[String]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("similarity").map(_.asInstanceOf[String]),
    values.get("split_queries_on_whitespace").map(_.asInstanceOf[Boolean]),
    values.get("store").map(_.asInstanceOf[Boolean]),
    values.get("term_vector").map(_.asInstanceOf[String])
  )

  def build(field: KeywordField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))

    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }

    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.index.foreach(builder.field("index", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.norms.foreach(builder.field("norms", _))
    field.normalizer.foreach(builder.field("normalizer", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.termVector.foreach(builder.field("term_vector", _))
    field.splitQueriesOnWhitespace.foreach(builder.field("split_queries_on_whitespace", _))

    builder.endObject()
  }
}
