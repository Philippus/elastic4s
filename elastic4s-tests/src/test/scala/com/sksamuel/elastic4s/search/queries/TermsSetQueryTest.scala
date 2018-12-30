package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import org.scalatest.{FlatSpec, Matchers}

class TermsSetQueryTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  client.execute {
    createIndex("randompeople").mappings(
      mapping("people").fields(
        textField("names"),
        floatField("required_matches")
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("randompeople/people") fields ("names" -> Seq("nelson","edmure","john"), "required_matches" -> 2),
      indexInto("randompeople/people") fields ("names" -> Seq("umber","rudolfus","byron"), "required_matches" -> 1)
    ).refresh(RefreshPolicy.Immediate)
  }.await

   // Test: Satisfying the requirements only one 'minimum should match' field (first one)
  "a terms set query with minimum shoud match field" should "return any documents that match at least two or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson","edmure","pete"), "required_matches")
    }.await.result
    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"names":["nelson","edmure","john"],"required_matches":2}""")
  }
  
  // Test: Satisfying the requirements only one 'minimum should match' field (second one)
  "a terms set query with minimum shoud match field" should "return any documents that match at least one term" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson","umber","sean"), "required_matches")
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"names":["umber","rudolfus","byron"],"required_matches":1}""")
  }

  // Test: Satisfying the requirements of both 'minimum should match' fields
  "a terms set query with minimum shoud match field" should "return any documents that match at least one or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson","edmure","rudolfus","christofer"), "required_matches")
    }.await.result
    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"names":["nelson","edmure","john"],"required_matches":2}""", """{"names":["umber","rudolfus","byron"],"required_matches":1}""")
  }

  // Test: Satisfying the requirements of the 'minimum should match' script for one document (first one)
  "a terms set query with minimum shoud match script" should "return any documents that match at least two terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson","edmure","pete"), script("Math.min(params.num_terms,doc['required_matches'].value)"))
    }.await.result
    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"names":["nelson","edmure","john"],"required_matches":2}""")
  }
  
  // Test: Satisfying the requirements of the 'minimum should match' script for both documents
  "a terms set query with minimum shoud match script" should "return any documents that match at least one or more terms" in {
    val resp = client.execute {
      search("randompeople") query termsSetQuery("names", Set("nelson","edmure","byron","pete"), script("Math.min(params.num_terms,doc['required_matches'].value)"))
    }.await.result
    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"names":["nelson","edmure","john"],"required_matches":2}""", """{"names":["umber","rudolfus","byron"],"required_matches":1}""")
  }  
} 
