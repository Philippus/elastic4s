package com.sksamuel.elastic4s.requests.analysis

import org.scalatest.{FunSuite, Matchers}

class AnalysisBuilderTest extends FunSuite with Matchers {

  test("custom analyzers") {
    val analysis = Analysis(
      List(
        CustomAnalyzer("my_analyzer", "my_uax_tokenizer", List("my_pattern_replace"), List("my_unique_filter", "my_truncate_filter"))
      ),
      tokenizers = List(
        UaxUrlEmailTokenizer("my_uax_tokenizer")
      ),
      tokenFilters = List(
        TruncateTokenFilter("my_truncate_filter", 123),
        UniqueTokenFilter("my_unique_filter", true)
      ),
      charFilters = List(
        PatternReplaceCharFilter(
          "my_pattern_replace",
          "qwe",
          "ert"
        )
      )
    )
    AnalysisBuilder.build(analysis).string() shouldBe """{"analysis":{"analyzer":{"my_analyzer":{"type":"custom","tokenizer":"my_uax_tokenizer","filter":["my_unique_filter","my_truncate_filter"],"char_filter":["my_pattern_replace"]}},"tokenizer":{"my_uax_tokenizer":{"type":"uax_url_email","max_token_length":255}},"char_filter":{"my_pattern_replace":{"type":"pattern_replace","pattern":"qwe","replacement":"ert"}},"filter":{"my_truncate_filter":{"type":"truncate","length":123},"my_unique_filter":{"type":"unique","only_on_same_position":true}}}}"""
  }

  test("custom normalizers") {
    val analysis = Analysis(
      Nil,
      normalizers = List(
        CustomNormalizer("my_normalizer", List("my_pattern_replace"), List("my_unique_filter", "my_truncate_filter"))
      ),
      tokenFilters = List(
        TruncateTokenFilter("my_truncate_filter", 123),
        UniqueTokenFilter("my_unique_filter", true)
      ),
      charFilters = List(
        PatternReplaceCharFilter(
          "my_pattern_replace",
          "qwe",
          "ert"
        )
      )
    )
    AnalysisBuilder.build(analysis).string() shouldBe """{"analysis":{"normalizer":{"my_normalizer":{"type":"custom","filter":["my_unique_filter","my_truncate_filter"],"char_filter":["my_pattern_replace"]}},"char_filter":{"my_pattern_replace":{"type":"pattern_replace","pattern":"qwe","replacement":"ert"}},"filter":{"my_truncate_filter":{"type":"truncate","length":123},"my_unique_filter":{"type":"unique","only_on_same_position":true}}}}"""
  }
}
