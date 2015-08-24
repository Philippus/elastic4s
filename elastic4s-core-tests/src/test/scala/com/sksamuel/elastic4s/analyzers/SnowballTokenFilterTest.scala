package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.anaylzers.TokenFilterDsl
import org.scalatest.{Matchers, WordSpec}

class SnowballTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "SnowballTokenFilter builder" should {
    "set language" in {
      snowballTokenFilter("testy").lang("vulcan").json.string shouldBe """{"language":"vulcan"}"""
    }
  }
}
