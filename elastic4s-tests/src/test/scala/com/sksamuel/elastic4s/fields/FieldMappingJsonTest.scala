package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.analysis.LanguageAnalyzers
import com.sksamuel.elastic4s.requests.mappings.{MappingBuilderFn, MappingDefinition}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FieldMappingJsonTest extends AnyFunSuite with Matchers {

  test("adding ElasticFields to mapping def") {

    val field1 = LongField(
      name = "myfield1",
      store = Some(true),
      boost = Some(1.2),
      ignoreMalformed = Some(true),
      copyTo = List("q", "er")
    )

    val field2 = TextField(
      name = "myfield2",
      analyzer = Some(LanguageAnalyzers.bengali),
      searchQuoteAnalyzer = Some(LanguageAnalyzers.english),
      similarity = Some("Classic1"),
      norms = Some(true)
    )

    val field3 = KeywordField(
      name = "myfield3",
      normalizer = Some("foo"),
      copyTo = List("q", "er"),
      ignoreAbove = Some(4),
      indexOptions = Some("freqs"),
      similarity = Some("Classic1"),
      norms = Some(true)
    )

    val mapping = MappingDefinition(properties = List(field1, field2, field3))
    MappingBuilderFn.build(mapping).string() shouldBe """{"properties":{"myfield1":{"type":"long","copy_to":["q","er"],"boost":1.2,"store":true,"ignore_malformed":true},"myfield2":{"type":"text","analyzer":"bengali","norms":true,"search_quote_analyzer":"english","similarity":"Classic1"},"myfield3":{"type":"keyword","copy_to":["q","er"],"ignore_above":4,"index_options":"freqs","norms":true,"normalizer":"foo","similarity":"Classic1"}}}"""
  }

  test("MappingBuilderFn should throw an exception if multiple properties with the same field name") {

    val field1 = LongField(
      name = "myfield",
      copyTo = List("q", "er")
    )

    val field2 = TextField(
      name = "myfield",
      norms = Some(true)
    )

    val mapping = MappingDefinition(properties = List(field1, field2))
    intercept[RuntimeException] {
      MappingBuilderFn.build(mapping).string()
    }
  }
}
