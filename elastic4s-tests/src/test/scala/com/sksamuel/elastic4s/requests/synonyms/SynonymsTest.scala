package com.sksamuel.elastic4s.requests.synonyms

import scala.util.Try

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.handlers.synonyms.{UpdateSynonymRuleBodyFn, UpdateSynonymsBodyFn}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SynonymsTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests {
  Try {
    client.execute {
      deleteSynonymsSet("my-synonyms-set")
    }.await.result
  }

  private val rules = Seq(
    SynonymRule(id = Some("test-1"), synonyms = "hello, hi"),
    SynonymRule("bye, goodbye"),
    SynonymRule(id = Some("test-2"), synonyms = "test => check")
  )

  // ---- request building (all fields) ----

  "CreateOrUpdateSynonymsSetRequest" should "build the body from all synonym rules" in {
    UpdateSynonymsBodyFn(CreateOrUpdateSynonymsSetRequest("my-synonyms-set", rules)).string shouldBe
      """{"synonyms_set":[{"id":"test-1","synonyms":"hello, hi"},{"synonyms":"bye, goodbye"},{"id":"test-2","synonyms":"test => check"}]}"""
  }

  it should "wire refresh and append into the request params" in {
    val request = CreateOrUpdateSynonymsSetRequest(
      "my-synonyms-set",
      rules,
      refresh = Some(true),
      append = Some(false)
    )
    val built   = UpdateSynonymsSetHandler.build(request)

    built.method shouldBe "PUT"
    built.endpoint shouldBe "/_synonyms/my-synonyms-set"
    built.params shouldBe Map("refresh" -> "true", "append" -> "false")
    built.entity.map(_.get) shouldBe Some(UpdateSynonymsBodyFn(request).string)
  }

  "GetSynonymsSetRequest" should "wire from, size and searchAfter into the request params" in {
    val request = GetSynonymsSetRequest("my-synonyms-set")
      .from(5)
      .size(10)
      .searchAfter("test-1")
    val built   = GetSynonymsSetHandler.build(request)

    built.method shouldBe "GET"
    built.endpoint shouldBe "/_synonyms/my-synonyms-set"
    built.params shouldBe Map("from" -> "5", "size" -> "10", "search_after" -> "test-1")
  }

  "CreateOrUpdateSynonymRuleRequest" should "build the body and wire refresh into the request params" in {
    val request = CreateOrUpdateSynonymRuleRequest(
      "my-synonyms-set",
      "test-1",
      "hello, hi, howdy",
      refresh = Some(true)
    )
    val built   = UpdateSynonymRuleHandler.build(request)

    built.method shouldBe "PUT"
    built.endpoint shouldBe "/_synonyms/my-synonyms-set/test-1"
    built.params shouldBe Map("refresh" -> "true")
    UpdateSynonymRuleBodyFn(request).string shouldBe """{"synonyms":"hello, hi, howdy"}"""
    built.entity.map(_.get) shouldBe Some(UpdateSynonymRuleBodyFn(request).string)
  }

  "DeleteSynonymRuleRequest" should "wire refresh into the request params" in {
    val request = DeleteSynonymRuleRequest("my-synonyms-set", "test-1", refresh = Some(true))
    val built   = DeleteSynonymRuleHandler.build(request)

    built.method shouldBe "DELETE"
    built.endpoint shouldBe "/_synonyms/my-synonyms-set/test-1"
    built.params shouldBe Map("refresh" -> "true")
  }

  // ---- integration ----

  "synonyms" should "create a new set" in {
    val resp = client.execute {
      createOrUpdateSynonymsSet("my-synonyms-set", rules)
    }.await.result

    resp.result shouldBe "created"
  }

  it should "return a created set" in {
    val resp = client.execute {
      getSynonymsSet("my-synonyms-set")
    }.await.result
    resp.count shouldBe 3
    resp.synonymsSet.map(_.synonyms).toSet shouldBe Set("hello, hi", "bye, goodbye", "test => check")
  }

  it should "page a created set with from, size and searchAfter" in {
    val resp = client.execute {
      getSynonymsSet("my-synonyms-set").size(1).searchAfter("test-1")
    }.await.result
    resp.synonymsSet.size shouldBe 1
  }

  it should "handle errors" in {
    client.execute {
      getSynonymsSet("not-a-set")
    }.await.isError shouldBe true
  }

  it should "list all sets" in {
    val resp = client.execute {
      listSynonymsSet()
    }.await.result

    resp.count shouldBe 1
    resp.results.size shouldBe 1
    resp.results.map(_.synonymsSet).head shouldBe "my-synonyms-set"
    resp.results.map(_.count).head shouldBe 3
  }

  it should "append to an existing set" in {
    val resp = client.execute {
      CreateOrUpdateSynonymsSetRequest(
        "my-synonyms-set",
        Seq(SynonymRule(id = Some("test-3"), synonyms = "yes, yeah")),
        append = Some(true)
      )
    }.await.result

    resp.result shouldBe "updated"

    val get = client.execute {
      getSynonymsSet("my-synonyms-set")
    }.await.result
    get.count shouldBe 4
  }

  it should "update an existing rule" in {
    val resp = client.execute {
      upsertSynonymRule("my-synonyms-set", "test-1", "hello, hi, howdy")
    }.await.result

    resp.result shouldBe "updated"
  }

  it should "delete a synonym rule" in {
    val resp = client.execute {
      deleteSynonymRule("my-synonyms-set", "test-1")
    }.await.result

    resp.result shouldBe "deleted"
  }

  it should "delete a set" in {
    client.execute {
      deleteSynonymsSet("my-synonyms-set")
    }.await.result

    val resp = client.execute {
      listSynonymsSet()
    }.await.result

    resp.count shouldBe 0
  }
}
