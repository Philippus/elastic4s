package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{FlatSpec, Matchers}

class CustomAnalyzerTest extends FlatSpec with Matchers {

  "CustomAnalyzer" should "support predefined tokenizers and filters" in {
    CustomAnalyzerDefinition(
      "mygerman",
      PredefinedTokenizer("standard"),
      PredefinedTokenFilter("lowercase"),
      PredefinedTokenFilter("german_stop"),
      PredefinedTokenFilter("german_keywords"),
      PredefinedTokenFilter("german_normalization"),
      PredefinedTokenFilter("german_stemmer")
    ).buildWithName().string shouldBe
      """{"mygerman":{"type":"custom","tokenizer":"standard","filter":["lowercase","german_stop","german_keywords","german_normalization","german_stemmer"]}}"""
  }
}
