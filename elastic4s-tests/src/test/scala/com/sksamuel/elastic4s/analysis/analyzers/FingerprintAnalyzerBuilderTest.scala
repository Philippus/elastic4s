package com.sksamuel.elastic4s.analysis.analyzers

import com.sksamuel.elastic4s.analysis.FingerprintAnalyzer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FingerprintAnalyzerBuilderTest extends AnyWordSpec with Matchers {

  "FingerprintAnalyzer" should {
    "build json" in {
      FingerprintAnalyzer("testy").separator("-").maxOutputSize(123).stopwords(
        "a",
        "z"
      ).build.string shouldBe """{"type":"fingerprint","separator":"-","stopwords":["a","z"],"max_output_size":123}"""
    }
  }
}
