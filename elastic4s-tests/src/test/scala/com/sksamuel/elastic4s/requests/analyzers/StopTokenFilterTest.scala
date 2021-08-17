package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StopTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "StopTokenFilter builder" should {
    "set stop words" in {
      stopTokenFilter("testy").stopwords("boo", "foo").json.string() shouldBe """{"type":"stop","stopwords":["boo","foo"]}"""
    }
    "set ignore case" in {
      stopTokenFilter("testy").ignoreCase(true).json.string() shouldBe """{"type":"stop","ignore_case":true}"""
    }
    "set remove trailing" in {
      stopTokenFilter("testy").removeTrailing(true).json.string() shouldBe """{"type":"stop","remove_trailing":true}"""
    }
  }
}
