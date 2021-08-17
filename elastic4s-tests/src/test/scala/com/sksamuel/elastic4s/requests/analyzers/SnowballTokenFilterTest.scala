package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SnowballTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "SnowballTokenFilter builder" should {
    "set language" in {
      snowballTokenFilter("testy", "vulcan").json.string() shouldBe """{"type":"snowball","language":"vulcan"}"""
    }
  }
}
