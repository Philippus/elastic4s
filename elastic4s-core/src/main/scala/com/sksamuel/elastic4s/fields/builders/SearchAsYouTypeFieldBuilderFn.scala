package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.SearchAsYouTypeField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object SearchAsYouTypeFieldBuilderFn {

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
