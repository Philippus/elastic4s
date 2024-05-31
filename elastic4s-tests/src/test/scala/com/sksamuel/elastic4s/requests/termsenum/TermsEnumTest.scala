package com.sksamuel.elastic4s.requests.termsenum

import scala.util.Try

import com.sksamuel.elastic4s.handlers.termsenum.TermsEnumBodyFn
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TermsEnumTest extends AnyWordSpec with Matchers with DockerTests {
  Try {
    client.execute {
      deleteIndex("test")
    }.await
  }

  client.execute {
    createIndex("test").mapping(
      properties(
        keywordField("tags"),
      )
    )
  }.await

  client.execute {
    indexInto("test") fields(
      "tags" -> "kibana"
    ) refresh RefreshPolicy.WaitFor
  }.await

  "a minimal terms enum query" should {
    "return proper response" in {
      val resp = client.execute {
        termsEnum("test", "tags", "kiba")
      }.await.result
      resp.terms shouldBe Seq("kibana")
      resp.isComplete shouldBe true
    }

   val termsEnumRequest = TermsEnumRequest("test", "tags")
      .string("kiba")
      .size(3)
      .timeout("1m")
      .caseInsensitive(false)
      .indexFilter(TermQuery("tags", "kiba"))
      .searchAfter("kiba")

    "support all fields in the builder" in {
      TermsEnumBodyFn(termsEnumRequest).string shouldEqual
        """{"field":"tags","string":"kiba","size":3,"timeout":"1m","case_insensitive":false,"index_filter":{"term":{"tags":{"value":"kiba"}}},"search_after":"kiba"}"""
    }

    "return a response when all fields are present" in {
      val resp = client.execute {
        termsEnumRequest
      }.await.result
      resp.terms shouldBe Seq("kibana")
      resp.isComplete shouldBe true
    }
  }
}
