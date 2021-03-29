package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class TermsSetQueryTest
  extends AnyFlatSpec
    with DockerTests
    with Matchers {

  Try {
    client.execute {
      deleteIndex("randompeople")
    }.await
  }

  Try {
    client.execute {
      createIndex("randompeople").mapping(
        mapping(
          textField("names"),
          floatField("required_matches")
        )
      )
    }.await
  }

  client.execute {
    bulk(
      indexInto("randompeople") fields("names" -> Seq("nelson", "edmure", "john"), "required_matches" -> 2),
      indexInto("randompeople") fields("names" -> Seq("umber", "rudolfus", "byron"), "required_matches" -> 1)
    ).refresh(RefreshPolicy.Immediate)
  }.await

  // Test: Satisfying the requirements only one 'minimum should match' field (first one)
  "a terms set query with minimum shoud match field" should "return any documents that match at least two or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson", "edmure", "pete"), "required_matches")
    }.await.result
    val map = resp.hits.hits.head.sourceAsMap
    map("names") shouldBe List("nelson", "edmure", "john")
    map("required_matches") shouldBe 2
  }

  // Test: Satisfying the requirements only one 'minimum should match' field (second one)
  "a terms set query with minimum shoud match field" should "return any documents that match at least one term" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson", "umber", "sean"), "required_matches")
    }.await.result
    val map = resp.hits.hits.head.sourceAsMap
    map("names") shouldBe List("umber", "rudolfus", "byron")
    map("required_matches") shouldBe 1
  }

  // Test: Satisfying the requirements of both 'minimum should match' fields
  "a terms set query with minimum shoud match field" should "return any documents that match at least one or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson", "edmure", "rudolfus", "christofer"), "required_matches")
    }.await.result
    resp.hits.hits.head.sourceAsMap("names") shouldBe List("nelson", "edmure", "john")
    resp.hits.hits.head.sourceAsMap("required_matches") shouldBe 2
    resp.hits.hits.apply(1).sourceAsMap("names") shouldBe List("umber", "rudolfus", "byron")
    resp.hits.hits.apply(1).sourceAsMap("required_matches") shouldBe 1
  }

  // Test: Satisfying the requirements of the 'minimum should match' script for one document (first one)
  "a terms set query with minimum shoud match script" should "return any documents that match at least two terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson", "edmure", "pete"), script("Math.min(params.num_terms,doc['required_matches'].value)"))
    }.await.result
    resp.hits.hits.head.sourceAsMap("names") shouldBe List("nelson","edmure","john")
    resp.hits.hits.head.sourceAsMap("required_matches") shouldBe 2
  }

  // Test: Satisfying the requirements of the 'minimum should match' script for both documents
  "a terms set query with minimum shoud match script" should "return any documents that match at least one or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson", "edmure", "byron", "pete"), script("Math.min(params.num_terms,doc['required_matches'].value)"))
    }.await.result
    resp.hits.hits.head.sourceAsMap("names") shouldBe List("nelson","edmure","john")
    resp.hits.hits.head.sourceAsMap("required_matches") shouldBe 2
    resp.hits.hits.apply(1).sourceAsMap("names") shouldBe List("umber","rudolfus","byron")
    resp.hits.hits.apply(1).sourceAsMap("required_matches") shouldBe 1
  }
}
