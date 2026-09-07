package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.jackson.JacksonSupport
import com.sksamuel.elastic4s.handlers.fields
import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IcuCollationKeywordFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "text field def" should "support text properties" in {
    val field = IcuCollationKeywordField(
      name = "myfield",
      language = Some("ca"),
      country = Some("ES"),
      variant = Some("@collation=phonebook"),
      strength = Some("primary"),
      decomposition = Some("no"),
      alternate = Some("shifted"),
      caseLevel = Some(true),
      caseFirst = Some("lower"),
      numeric = Some(true),
      variableTop = Some("."),
      hiraganaQuaternaryMode = Some(true),
      index = Some(true),
      docValues = Some(true),
      ignoreAbove = Some(42),
      nullValue = Some("NULL"),
      fields = List(KeywordField("raw", ignoreAbove = Some(256))),
      store = Some(true)
    )

    fields.ElasticFieldBuilderFn(field).string shouldBe
      """{"type":"icu_collation_keyword","language":"ca","country":"ES","variant":"@collation=phonebook","strength":"primary","decomposition":"no","alternate":"shifted","case_level":true,"case_first":"lower","numeric":true,"variable_top":".","hiragana_quaternary_mode":true,"fields":{"raw":{"type":"keyword","ignore_above":256}},"index":true,"doc_values":true,"ignore_above":42,"null_value":"NULL","store":true}"""

    ElasticFieldBuilderFn.construct(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](fields.ElasticFieldBuilderFn(field).string)
    ) shouldBe field
  }
}
