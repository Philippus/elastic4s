package com.sksamuel.elastic4s.requests.indexes

import java.util.UUID

import com.sksamuel.elastic4s.requests.common.VersionType.External
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, VersionType}
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.Indexable
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class IndexTest extends WordSpec with Matchers with DockerTests {

  case class Phone(name: String, speed: String)

  implicit object PhoneIndexable extends Indexable[Phone] {
    override def json(t: Phone): String = s"""{ "name" : "${t.name}", "speed" : "${t.speed}" }"""
  }

  Try {
    client.execute {
      deleteIndex("electronics")
    }.await
  }

  client.execute {
    createIndex("electronics").mappings(mapping("electronics"))
  }.await

  client.execute {
    bulk(
      indexInto("electronics" / "electronics").fields(Map("name" -> "galaxy", "screensize" -> 5)).withId("55A").version(42l).versionType(VersionType.External),
      indexInto("electronics" / "electronics").fields(Map("name" -> "razor", "colours" -> Array("white", "blue"))),
      indexInto("electronics" / "electronics").fields(Map("name" -> "iphone", "colour" -> null)),
      indexInto("electronics" / "electronics").fields(Map("name" -> "m9", "locations" -> Array(Map("id" -> "11", "name" -> "manchester"), Map("id" -> "22", "name" -> "sheffield")))),
      indexInto("electronics" / "electronics").fields(Map("name" -> "iphone2", "models" -> Map("5s" -> Array("standard", "retina")))),
      indexInto("electronics" / "electronics").fields(Map("name" -> "pixel", "apps" -> Map("maps" -> "google maps", "email" -> null))),
      indexInto("electronics" / "electronics").source(Phone("nokia blabble", "4g"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "an index request" should {
    "index fields" in {
      client.execute {
        search("electronics").query(matchQuery("name", "galaxy"))
      }.await.result.totalHits shouldBe 1
    }
    "support index names with +" in {
      client.execute {
        createIndex("hello+world").mappings(mapping("wobble"))
      }.await
      client.execute {
        indexInto("hello+world/wobble").fields(Map("foo" -> "bar")).withId("a").refreshImmediately
      }.await
      client.execute {
        search("hello+world").matchAllQuery()
      }.await.result.totalHits shouldBe 1
    }
    "support / in ids" in {
      client.execute {
        createIndex("indexidtest").mappings(mapping("wobble"))
      }.await
      client.execute {
        indexInto("indexidtest/wobble").fields(Map("foo" -> "bar")).withId("a/b").refreshImmediately
      }.await
      client.execute {
        search("indexidtest").matchAllQuery()
      }.await.result.totalHits shouldBe 1
      client.execute {
        get("indexidtest", "wobble", "a/b")
      }.await.result.exists shouldBe true
    }
    "support external versions" in {
      val found = client.execute {
        search("electronics").query(matchQuery("name", "galaxy")).version(true)
      }.await.result.hits.hits(0)
      found.id shouldBe "55A"
      found.version shouldBe 42l
    }
    "handle custom id" in {
      client.execute {
        search("electronics").query(idsQuery("55A"))
      }.await.result.totalHits shouldBe 1
    }
    "handle numbers" in {
      client.execute {
        search("electronics").query(termQuery("screensize", 5))
      }.await.result.totalHits shouldBe 1
    }
    "handle arrays" in {
      client.execute {
        search("electronics").query(matchQuery("name", "razor"))
      }.await.result.hits.hits.head.sourceAsMap shouldBe Map("name" -> "razor", "colours" -> List("white", "blue"))
    }
    "handle nested arrays" in {
      val hit = client.execute {
        search("electronics").query(matchQuery("name", "iphone2"))
      }.await.result.hits.hits.head
      hit.sourceAsMap("models") shouldBe Map("5s" -> List("standard", "retina"))
    }
    "handle arrays of maps" in {
      val hit = client.execute {
        search("electronics").query(matchQuery("name", "m9"))
      }.await.result.hits.hits.head
      hit.sourceAsMap("locations") shouldBe
        Seq(
          Map("id" -> "11", "name" -> "manchester"),
          Map("id" -> "22", "name" -> "sheffield")
        )
    }
    "handle null fields" in {
      client.execute {
        search("electronics").query(matchQuery("name", "iphone"))
      }.await.result.hits.hits.head.sourceAsMap shouldBe Map("colour" -> null, "name" -> "iphone")
    }
    "handle nested null fields" in {
      val hit = client.execute {
        search("electronics").query(matchQuery("name", "pixel"))
      }.await.result.hits.hits.head
      hit.sourceAsMap("apps") shouldBe Map("maps" -> "google maps", "email" -> null)
    }
    "index from indexable typeclass" in {
      client.execute {
        search("electronics").query(termQuery("speed", "4g"))
      }.await.result.totalHits shouldBe 1
    }
    "create aliases with index" in {
      val id = UUID.randomUUID()
      val indexName = s"electronics-$id"
      client.execute {
        createIndex(indexName).mappings(mapping("electronics"))
          .alias("alias_1")
          .alias("alias_2")
      }.await
      val index = client.execute {
        getIndex(indexName)
      }.await.result.apply(indexName)
      index.aliases should contain key "alias_1"
      index.aliases should contain key "alias_2"

      client.execute {
        deleteIndex(indexName)
      }.await
    }
    "return created status" in {
      val result = client.execute {
        indexInto("electronics" / "electronics").fields("name" -> "super phone").refresh(RefreshPolicy.Immediate)
      }.await
      result.result.result shouldBe "created"
    }
    "return OK status if the document already exists" in {
      val id = UUID.randomUUID().toString
      client.execute {
        indexInto("electronics" / "electronics").fields("name" -> "super phone").withId(id).refresh(RefreshPolicy.Immediate)
      }.await
      val result = client.execute {
        indexInto("electronics" / "electronics").fields("name" -> "super phone").withId(id).refresh(RefreshPolicy.Immediate)
      }.await
      result.result.result shouldBe "updated"
    }
    "handle update concurrency" in {
      val id = UUID.randomUUID.toString
      client.execute {
        indexInto("electronics" / "electronics")
          .fields("name" -> "super phone")
          .withId(id)
          .version(2l)
          .versionType(External)
          .refresh(RefreshPolicy.Immediate)
      }.await
      val result = client.execute {
        indexInto("electronics" / "electronics")
          .fields("name" -> "super phone")
          .withId(id)
          .version(2l)
          .versionType(External)
          .refresh(RefreshPolicy.Immediate)
      }.await
      result.error.toString should include ("version_conflict_engine_exception")
    }
    "return Left when the request has an invalid index name" in {
      val result = client.execute {
        indexInto("**1w11oowo/!!!!o_$$$")
      }.await
      result.error should not be null
    }
  }
}
