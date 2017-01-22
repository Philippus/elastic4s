package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

import scala.collection.JavaConverters._

case class Phone(name: String, speed: String)

class IndexTcpTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  implicit object PhoneIndexable extends Indexable[Phone] {
    override def json(t: Phone): String = s"""{ "name" : "${t.name}", "speed" : "${t.speed}" }"""
  }
  val phone = Phone("nokia blabble", "4g")

  client.execute {
    createIndex("electronics").mappings(mapping("phone"))
  }.await

  client.execute {
    bulk(
      indexInto("electronics/phone").fields(Map("name" -> "galaxy", "screensize" -> 5)).withId("55A"),
      indexInto("electronics" / "phone").fields(Map("name" -> "razor", "colours" -> Array("white", "blue"))),
      indexInto("electronics" / "phone").fields(Map("name" -> "iphone", "colour" -> null)),
      indexInto("electronics" / "phone").fields(Map("name" -> "m9", "locations" -> Array(Map("id" -> "11", "name" -> "manchester"), Map("id" -> "22", "name" -> "sheffield")))),
      indexInto("electronics" / "phone").fields(Map("name" -> "iphone2", "models" -> Map("5s" -> Array("standard", "retina")))),
      indexInto("electronics" / "phone").fields(Map("name" -> "pixel", "apps" -> Map("maps" -> "google maps", "email" -> null))),
      indexInto("electronics" / "phone").source(phone)
    ).refresh(RefreshPolicy.IMMEDIATE)
  }
  blockUntilCount(6, "electronics")

  "an index request" should {
    "index fields" in {
      client.execute {
        search("electronics" / "phone").query(matchQuery("name", "galaxy"))
      }.await.totalHits shouldBe 1
    }
    "handle custom id" in {
      client.execute {
        search("electronics" / "phone").query(idsQuery("55A"))
      }.await.totalHits shouldBe 1
    }
    "handle numbers" in {
      client.execute {
        search("electronics" / "phone").query(termQuery("screensize", 5))
      }.await.totalHits shouldBe 1
    }
    "handle arrays" in {
      client.execute {
        search("electronics" / "phone").query(matchQuery("name", "razor"))
      }.await.hits.head.sourceAsMap shouldBe Map("name" -> "razor", "colours" -> java.util.Arrays.asList("white", "blue"))
    }
    "handle nested arrays" in {
      val hit = client.execute {
        search("electronics" / "phone").query(matchQuery("name", "iphone2"))
      }.await.hits.head
      hit.sourceAsMap("models").asInstanceOf[java.util.Map[String, Any]].asScala.toMap shouldBe
        Map("5s" -> java.util.Arrays.asList("standard", "retina"))
    }
    "handle arrays of maps" in {
      val hit = client.execute {
        search("electronics" / "phone").query(matchQuery("name", "m9"))
      }.await.hits.head
      hit.sourceAsMap("locations").asInstanceOf[java.util.List[Any]].asScala shouldBe
        Seq(
          Map("id" -> "11", "name" -> "manchester").asJava,
          Map("id" -> "22", "name" -> "sheffield").asJava
        )
    }
    "handle null fields" in {
      client.execute {
        search("electronics" / "phone").query(matchQuery("name", "iphone"))
      }.await.hits.head.sourceAsMap shouldBe Map("colour" -> null, "name" -> "iphone")
    }
    "handle nested null fields" in {
      val hit = client.execute {
        search("electronics" / "phone").query(matchQuery("name", "pixel"))
      }.await.hits.head
      hit.sourceAsMap("apps").asInstanceOf[java.util.Map[String, Any]].asScala.toMap shouldBe
        Map("maps" -> "google maps", "email" -> null)
    }
    "index from indexable typeclass" in {
      client.execute {
        search("electronics" / "phone").query(termQuery("speed", "4g"))
      }.await.totalHits shouldBe 1
    }
  }
}
