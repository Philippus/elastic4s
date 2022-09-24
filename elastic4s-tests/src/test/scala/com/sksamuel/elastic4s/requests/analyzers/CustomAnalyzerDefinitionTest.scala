package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CustomAnalyzerDefinitionTest extends AnyFlatSpec with Matchers {

  "CustomAnalyzerDefinition" should "build correct json" in {
    CustomAnalyzerDefinition("mycustom", KeywordTokenizer, KStemTokenFilter, ApostropheTokenFilter)
      .build().string shouldBe """{"type":"custom","tokenizer":"keyword","filter":["kstem","apostrophe"]}"""
  }
}
