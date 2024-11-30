package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class KeywordMarkerTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "KeywordMarkerTokenFilter builder" should {
    "not set any defaults" in {
      keywordMarkerTokenFilter("testy").json.string shouldBe """{"type":"keyword_marker"}"""
    }
    "set keywords" in {
      keywordMarkerTokenFilter("testy").keywords("foo", "bar").json.string shouldBe """{"type":"keyword_marker","keywords":["foo","bar"]}"""
    }
    "set keywords path" in {
      keywordMarkerTokenFilter("testy").keywordsPath("config/keywords.txt").json.string shouldBe """{"type":"keyword_marker","keywords_path":"config/keywords.txt"}"""
    }
    "set keywords pattern" in {
      keywordMarkerTokenFilter("testy").keywordsPattern("pattern").json.string shouldBe """{"type":"keyword_marker","keywords_pattern":"pattern"}"""
    }
    "set ignore case" in {
      keywordMarkerTokenFilter("testy").ignoreCase(true).json.string shouldBe """{"type":"keyword_marker","ignore_case":true}"""
    }
  }
}
