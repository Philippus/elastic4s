package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.ElasticDsl._
import org.scalatest.{FunSuite, Matchers}

class NestedQueryBodyFnTest extends FunSuite with Matchers {

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
    NestedQueryBodyFn(query).string() shouldBe
      "{\"nested\":{\"path\":\"messages\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"highlight\":{\"fields\":{\"messages.text\":{\"matched_fields\":[\"messages.text\",\"messages.japanese\"]}}}}}}"
  }
  test("inner highlight with 'highlighterType' generates proper 'type' field.") {
    val query = nestedQuery("messages", matchAllQuery()) inner {
      innerHits("") highlighting {
        highlight("messages.text")
          .highlighterType("fvh")
      }
    }
    NestedQueryBodyFn(query).string() shouldBe
      "{\"nested\":{\"path\":\"messages\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"highlight\":{\"fields\":{\"messages.text\":{\"type\":\"fvh\"}}}}}}"
  }
}
