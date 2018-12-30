package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class ShingleTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "ShingleTokenFilter builder" should {
    "not set any defaults" in {
      shingleTokenFilter("testy").json.string shouldBe """{"type":"shingle"}"""
    }
    "set max shingle size" in {
      shingleTokenFilter("testy").maxShingleSize(10).json.string shouldBe """{"type":"shingle","max_shingle_size":10}"""
    }
    "set min shingle size" in {
      shingleTokenFilter("testy").minShingleSize(11).json.string shouldBe """{"type":"shingle","min_shingle_size":11}"""
    }
    "set output unigrams" in {
      shingleTokenFilter("testy").outputUnigrams(false).json.string shouldBe """{"type":"shingle","output_unigrams":false}"""
    }
    "set output unigrams if no shingles" in {
      shingleTokenFilter("testy").outputUnigramsIfNoShingles(true).json.string shouldBe """{"type":"shingle","output_unigrams_if_no_shingles":true}"""
    }
    "set token separator" in {
      shingleTokenFilter("testy").tokenSeparator("/").json.string shouldBe """{"type":"shingle","token_separator":"/"}"""
    }
    "set filler token" in {
      shingleTokenFilter("testy").fillerToken("-").json.string shouldBe """{"type":"shingle","filler_token":"-"}"""
    }
  }
}
