package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.IcuCollationKeywordField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object IcuCollationKeywordFieldBuilderFn {

  def build(field: IcuCollationKeywordField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.language.foreach(builder.field("language", _))
    field.country.foreach(builder.field("country", _))
    field.variant.foreach(builder.field("variant", _))
    field.strength.foreach(builder.field("strength", _))
    field.decomposition.foreach(builder.field("decomposition", _))
    field.alternate.foreach(builder.field("alternate", _))
    field.caseLevel.foreach(builder.field("case_level", _))
    field.caseFirst.foreach(builder.field("case_first", _))
    field.numeric.foreach(builder.field("numeric", _))
    field.variableTop.foreach(builder.field("variable_top", _))
    field.hiraganaQuaternaryMode.foreach(builder.field("hiragana_quaternary_mode", _))
    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }
    field.index.foreach(builder.field("index", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))

    builder.endObject()
  }
}
