package com.sksamuel.elastic4s.requests.synonyms

import scala.util.Try

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SynonymsTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests {
  Try {
    client.execute {
      deleteSynonymsSet("my-synonyms-set")
    }.await.result
  }

  "synonyms" should "create a new set" in {
    val resp = client.execute {
      createOrUpdateSynonymsSet("my-synonyms-set", Seq(SynonymRule(id = Some("test-1"), synonyms = "hello, hi"), SynonymRule("bye, goodbye"), SynonymRule(id = Some("test-2"), synonyms = "test => check")))
    }.await.result

    resp.result shouldBe "created"
  }

  it should "return a created set" in {
    val resp = client.execute {
      getSynonymsSet("my-synonyms-set")
    }.await.result

    resp.count shouldBe 3
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

  it should "delete a set" in {
    client.execute {
      deleteSynonymsSet("my-synonyms-set")
    }.await.result

    val resp = client.execute {
      listSynonymsSet()
    }.await.result

    resp.count shouldBe 0
  }

  it should "update an existing rule" in {
    client.execute {
      createOrUpdateSynonymsSet("my-synonyms-set", Seq(SynonymRule(id = Some("test-1"), synonyms = "hello, hi"), SynonymRule("bye, goodbye"), SynonymRule(id = Some("test-2"), synonyms = "test => check")))
    }.await.result

    val resp = client.execute {
      upsertSynonymRule("my-synonyms-set", "test-1", "hello, hi, howdy")
    }.await.result

    resp.result shouldBe "updated"
  }

  it should "delete a synonym rule" in {
    client.execute {
      createOrUpdateSynonymsSet("my-synonyms-set", Seq(SynonymRule(id = Some("test-1"), synonyms = "hello, hi"), SynonymRule("bye, goodbye"), SynonymRule(id = Some("test-2"), synonyms = "test => check")))
    }.await.result

    val resp = client.execute {
      deleteSynonymRule("my-synonyms-set", "test-1")
    }.await.result

    resp.result shouldBe "deleted"
  }
}
