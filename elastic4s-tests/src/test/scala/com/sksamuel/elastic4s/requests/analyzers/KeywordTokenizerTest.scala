package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.api.TokenizerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class KeywordTokenizerTest extends AnyWordSpec with TokenizerApi with Matchers with ElasticDsl {

  "KeywordTokenizer builder" should {
    "set buffer size" in {
      keywordTokenizer("testy").bufferSize(123).json.string() shouldBe """{"type":"keyword","bufferSize":123}"""
    }
  }
}
