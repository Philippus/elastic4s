package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.IcuCollationKeywordField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object IcuCollationKeywordFieldBuilderFn {

  def toField(name: String, values: Map[String, Any]): IcuCollationKeywordField =
    IcuCollationKeywordField(
      name = name,
      language = values.get("language").map(_.asInstanceOf[String]),
      country = values.get("country").map(_.asInstanceOf[String]),
      variant = values.get("variant").map(_.asInstanceOf[String]),
      strength = values.get("strength").map(_.asInstanceOf[String]),
      decomposition = values.get("decomposition").map(_.asInstanceOf[String]),
      alternate = values.get("alternate").map(_.asInstanceOf[String]),
      caseLevel = values.get("case_level").map(_.asInstanceOf[Boolean]),
      caseFirst = values.get("case_first").map(_.asInstanceOf[String]),
      numeric = values.get("numeric").map(_.asInstanceOf[Boolean]),
      variableTop = values.get("variable_top").map(_.asInstanceOf[String]),
      hiraganaQuaternaryMode = values.get("hiragana_quaternary_mode").map(_.asInstanceOf[Boolean]),
      fields = values
        .get("fields")
        .map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (k, v) =>
          ElasticFieldBuilderFn.construct(k, v)
        }.toList)
        .getOrElse(List.empty),
      index = values.get("index").map(_.asInstanceOf[Boolean]),
      docValues = values.get("doc_values").map(_.asInstanceOf[Boolean]),
      ignoreAbove = values.get("ignore_above").map(_.asInstanceOf[Int]),
      nullValue = values.get("null_value").map(_.asInstanceOf[String]),
      store = values.get("store").map(_.asInstanceOf[Boolean])
    )

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
