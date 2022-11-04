package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.{ElasticApi, JacksonSupport}
import com.sksamuel.elastic4s.analysis.LanguageAnalyzers
import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextFieldTest extends AnyFlatSpec with Matchers with ElasticApi {

  "text field def" should "support text properties" in {
    val field = TextField(
      name = "myfield",
      analyzer = Some(LanguageAnalyzers.bengali),
      searchAnalyzer = Some(LanguageAnalyzers.norwegian),
      searchQuoteAnalyzer = Some(LanguageAnalyzers.english),
      fielddata = Some(true),
      store = Some(true),
      boost = Some(1.2),
      index = Some(true),
      copyTo = List("q", "er"),
      positionIncrementGap = Some(3),
      indexOptions = Some("freqs"),
      similarity = Some("Classic1"),
      norms = Some(true)
    )

    val jsonStringValue = """{"type":"text","analyzer":"bengali","boost":1.2,"copy_to":["q","er"],"index":true,"norms":true,"store":true,"fielddata":true,"position_increment_gap":3,"index_options":"freqs","search_analyzer":"norwegian","search_quote_analyzer":"english","similarity":"Classic1"}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
    ElasticFieldBuilderFn.construct(field.name, JacksonSupport.mapper.readValue[Map[String, Any]](jsonStringValue)) shouldBe (field)
  }
}
