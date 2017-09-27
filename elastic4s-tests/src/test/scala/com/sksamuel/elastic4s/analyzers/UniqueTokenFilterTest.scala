package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class UniqueTokenFilterTest extends WordSpec with AnalyzerApi with Matchers with TokenFilterDsl {

  "UniqueTokenFilter builder" should {
    "not set any defaults" in {
      uniqueTokenFilter("testy").json.string shouldBe """{"type":"unique"}"""
    }
    "set only same position" in {
      uniqueTokenFilter("testy").onlyOnSamePosition(true).json.string shouldBe """{"type":"unique","only_on_same_position":true}"""
    }
  }
}
