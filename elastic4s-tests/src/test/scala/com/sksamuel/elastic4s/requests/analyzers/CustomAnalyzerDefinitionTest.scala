package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{FlatSpec, Matchers}

class CustomAnalyzerDefinitionTest extends FlatSpec with Matchers {

  "CustomAnalyzerDefinition" should "build correct json" in {
    CustomAnalyzerDefinition("mycustom", KeywordTokenizer, KStemTokenFilter, ApostropheTokenFilter)
      .build().string() shouldBe """{"type":"custom","tokenizer":"keyword","filter":["kstem","apostrophe"]}"""
  }
}
