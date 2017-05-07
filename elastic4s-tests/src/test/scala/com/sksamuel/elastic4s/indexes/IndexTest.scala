package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class IndexTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  case class Phone(name: String, speed: String)

  implicit object PhoneIndexable extends Indexable[Phone] {
    override def json(t: Phone): String = s"""{ "name" : "${t.name}", "speed" : "${t.speed}" }"""
  }

  override protected def beforeRunTests() = {
    execute {
      createIndex("electronics").mappings(mapping("phone"))
    }.await

    execute {
      bulk(
        indexInto("electronics" / "phone").fields(Map("name" -> "galaxy", "screensize" -> 5)).withId("55A"),
        indexInto("electronics" / "phone").fields(Map("name" -> "razor", "colours" -> Array("white", "blue"))),
        indexInto("electronics" / "phone").fields(Map("name" -> "iphone", "colour" -> null)),
        indexInto("electronics" / "phone").fields(Map("name" -> "m9", "locations" -> Array(Map("id" -> "11", "name" -> "manchester"), Map("id" -> "22", "name" -> "sheffield")))),
        indexInto("electronics" / "phone").fields(Map("name" -> "iphone2", "models" -> Map("5s" -> Array("standard", "retina")))),
        indexInto("electronics" / "phone").fields(Map("name" -> "pixel", "apps" -> Map("maps" -> "google maps", "email" -> null))),
        indexInto("electronics" / "phone").source(Phone("nokia blabble", "4g"))
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }

  "an index request" should {
    "index fields" in {
      execute {
        search("electronics" / "phone").query(matchQuery("name", "galaxy"))
      }.await.totalHits shouldBe 1
    }
    "handle custom id" in {
      execute {
        search("electronics" / "phone").query(idsQuery("55A"))
      }.await.totalHits shouldBe 1
    }
    "handle numbers" in {
      execute {
        search("electronics" / "phone").query(termQuery("screensize", 5))
      }.await.totalHits shouldBe 1
    }
    "handle arrays" in {
      execute {
        search("electronics" / "phone").query(matchQuery("name", "razor"))
      }.await.hits.hits.head.sourceAsMap shouldBe Map("name" -> "razor", "colours" -> List("white", "blue"))
    }
    "handle nested arrays" in {
      val hit = execute {
        search("electronics" / "phone").query(matchQuery("name", "iphone2"))
      }.await.hits.hits.head
      hit.sourceAsMap("models") shouldBe Map("5s" -> List("standard", "retina"))
    }
    "handle arrays of maps" in {
      val hit = execute {
        search("electronics" / "phone").query(matchQuery("name", "m9"))
      }.await.hits.hits.head
      hit.sourceAsMap("locations") shouldBe
        Seq(
          Map("id" -> "11", "name" -> "manchester"),
          Map("id" -> "22", "name" -> "sheffield")
        )
    }
    "handle null fields" in {
      execute {
        search("electronics" / "phone").query(matchQuery("name", "iphone"))
      }.await.hits.hits.head.sourceAsMap shouldBe Map("colour" -> null, "name" -> "iphone")
    }
    "handle nested null fields" in {
      val hit = execute {
        search("electronics" / "phone").query(matchQuery("name", "pixel"))
      }.await.hits.hits.head
      hit.sourceAsMap("apps") shouldBe Map("maps" -> "google maps", "email" -> null)
    }
    "index from indexable typeclass" in {
      execute {
        search("electronics" / "phone").query(termQuery("speed", "4g"))
      }.await.totalHits shouldBe 1
    }
    "return created status" in {
      val result = execute {
        indexInto("electronics" / "phone").fields("name" -> "super phone").refresh(RefreshPolicy.IMMEDIATE)
      }.await
      result.created shouldBe true
      result.result shouldBe "created"
    }
  }
}
