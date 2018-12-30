package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{FlatSpec, Matchers}

class CustomNormalizerTest extends FlatSpec with Matchers {

  "CustomNormalizer" should "support predefined filters" in {
    CustomNormalizerDefinition(
      "mygerman",
      PredefinedTokenFilter("lowercase"),
      PredefinedTokenFilter("german_stop"),
      PredefinedTokenFilter("german_keywords"),
      PredefinedTokenFilter("german_normalization"),
      PredefinedTokenFilter("german_stemmer"),
      PredefinedCharFilter("german_charfilter")
    ).buildWithName().string shouldBe
      """{"mygerman":{"type":"custom","filter":["lowercase","german_stop","german_keywords","german_normalization","german_stemmer"],"char_filter":["german_charfilter"]}}"""
  }
}
