package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class StemmerTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "StemmerTokenFilter builder" should {
    "set language" in {
      stemmerTokenFilter("testy", "vulcan").json.string shouldBe """{"type":"stemmer","name":"vulcan"}"""
    }
  }
}
