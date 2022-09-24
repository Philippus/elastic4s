package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.SearchAsYouTypeField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object SearchAsYouTypeFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): SearchAsYouTypeField = SearchAsYouTypeField(
    name,
    values.get("analyzer").map(_.asInstanceOf[String]),
    values.get("search_analyzer").map(_.asInstanceOf[String]),
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("fielddata").map(_.asInstanceOf[Boolean]),
    values.get("ignore_above").map(_.asInstanceOf[Int]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("max_shingle_size").map(_.asInstanceOf[Int]),
    values.get("norms").map(_.asInstanceOf[Boolean]),
    values.get("similarity").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean]),
    values.get("term_vector").map(_.asInstanceOf[String])
  )


  def build(field: SearchAsYouTypeField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.index.foreach(builder.field("index", _))
    field.norms.foreach(builder.field("norms", _))
    field.store.foreach(builder.field("store", _))
    field.fielddata.foreach(builder.field("fielddata", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.termVector.foreach(builder.field("term_vector", _))
    field.maxShingleSize.foreach(builder.field("max_shingle_size", _))

    builder.endObject()
  }
}
