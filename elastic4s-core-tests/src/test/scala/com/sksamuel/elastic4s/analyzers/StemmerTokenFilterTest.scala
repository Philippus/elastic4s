package com.sksamuel.elastic4s2.analyzers

import org.scalatest.{Matchers, WordSpec}

class StemmerTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "StemmerTokenFilter builder" should {
    "set language" in {
      stemmerTokenFilter("testy").lang("vulcan").json.string shouldBe """{"type":"stemmer","name":"vulcan"}"""
    }
  }
}
