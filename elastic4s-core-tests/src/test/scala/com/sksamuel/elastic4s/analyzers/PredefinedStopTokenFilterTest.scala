package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class PredefinedStopTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "PredefinedStopTokenFilter builder" should {
    "set stop words" in {
      predefinedStopTokenFilter("testy").stopwords(NamedStopTokenFilter.Swedish).json.string shouldBe """{"type":"stop","stopwords":"_swedish_"}"""
    }
    "set ignore case" in {
      stopTokenFilter("testy").ignoreCase(true).json.string shouldBe """{"type":"stop","ignore_case":true}"""
    }
    "set remove trailing" in {
      stopTokenFilter("testy").removeTrailing(true).json.string shouldBe """{"type":"stop","remove_trailing":true}"""
    }
  }

}
