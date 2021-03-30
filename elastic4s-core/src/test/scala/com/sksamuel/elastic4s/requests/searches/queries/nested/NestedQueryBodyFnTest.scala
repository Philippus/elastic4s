package com.sksamuel.elastic4s.requests.searches.queries.nested

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.handlers.searches.queries.nested
import com.sksamuel.elastic4s.handlers.searches.queries.nested.NestedQueryBodyFn
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class NestedQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("it should creates specified query") {
    val query = nestedQuery("messages", matchAllQuery())
    NestedQueryBodyFn(query).string() shouldBe
      "{\"nested\":{\"path\":\"messages\",\"query\":{\"match_all\":{}}}}"
  }
  test("inner highlight with 'matchedMatchedFields' generates proper 'matched_fields' field as array field.") {
    val query = nestedQuery("messages", matchAllQuery()) inner {
      innerHits("") highlighting {
        highlight("messages.text")
          .matchedFields("messages.text", "messages.japanese")
      }
    }
    nested.NestedQueryBodyFn(query).string() shouldBe
      "{\"nested\":{\"path\":\"messages\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"highlight\":{\"fields\":{\"messages.text\":{\"matched_fields\":[\"messages.text\",\"messages.japanese\"]}}}}}}"
  }
  test("inner highlight with 'highlighterType' generates proper 'type' field.") {
    val query = nestedQuery("messages", matchAllQuery()) inner {
      innerHits("") highlighting {
        highlight("messages.text")
          .highlighterType("fvh")
      }
    }
    nested.NestedQueryBodyFn(query).string() shouldBe
      "{\"nested\":{\"path\":\"messages\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"highlight\":{\"fields\":{\"messages.text\":{\"type\":\"fvh\"}}}}}}"
  }
}
