package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.analysis.LanguageAnalyzers
import com.sksamuel.elastic4s.fields.builders.ElasticFieldBuilderFn
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
      ignoreAbove = Some(4),
      indexOptions = Some("freqs"),
      similarity = Some("Classic1"),
      norms = Some(true)
    )

    ElasticFieldBuilderFn(field).string() shouldBe """{"type":"text","analyzer":"bengali","boost":1.2,"copy_to":["q","er"],"index":true,"norms":true,"store":true,"fielddata":true,"position_increment_gap":3,"ignore_above":4,"index_options":"freqs","search_analyzer":"norwegian","search_quote_analyzer":"english","similarity":"Classic1"}"""
  }
}
