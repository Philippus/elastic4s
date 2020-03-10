package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StemmerTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "StemmerTokenFilter builder" should {
    "set language" in {
      stemmerTokenFilter("testy", "vulcan").json.string shouldBe """{"type":"stemmer","name":"vulcan"}"""
    }
  }
}
