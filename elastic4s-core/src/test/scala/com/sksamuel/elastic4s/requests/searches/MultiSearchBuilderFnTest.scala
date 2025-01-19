package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MultiSearchBuilderFnTest extends AnyWordSpec with Matchers {

  private val searchRequest: SearchRequest = search("someIndex")

  "MultiSearchBuilderFn" should {
    "build multisearch request without indices options" in {
      val searchStr = MultiSearchBuilderFn(multi(searchRequest))
      searchStr.linesIterator.next shouldBe """{"index":"someIndex"}"""
    }
    "build multisearch request with default ignore_unavailable indices option" in {
      val req = searchRequest indicesOptions IndicesOptionsRequest()
      MultiSearchBuilderFn(multi(req)).linesIterator.next shouldBe """{"index":"someIndex"}"""
    }
    "build multisearch request with ignore_unavailable indices option" in {
      val req = searchRequest indicesOptions IndicesOptionsRequest(ignoreUnavailable = true)
      MultiSearchBuilderFn(
        multi(req)
      ).linesIterator.next shouldBe """{"index":"someIndex","ignore_unavailable":"true"}"""

    }
  }
}
