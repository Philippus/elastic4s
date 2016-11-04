package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class IndexTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    createIndex("electronics").mappings(mapping("phone"))
  }.await

  "an index request" should {
    "index numbers" in {
      client.execute {
        indexInto("electronics" / "phone").fields(Map("screensize" -> 5))
      }
      blockUntilCount(1, "electronics")

      client.execute {
        search("electronics" / "phone").query(termQuery("screensize", 5))
      }.await.totalHits shouldBe 1
    }
    "index from indexable typeclass" in {

      case class Phone(name: String, speed: String)
      implicit object PhoneIndexable extends Indexable[Phone] {
        override def json(t: Phone): String = s"""{ "name" : "${t.name}", "speed" : "${t.speed}" }"""
      }
      val phone = Phone("nokia blabble", "4g")

      client.execute {
        indexInto("electronics" / "phone").source(phone)
      }
      blockUntilCount(2, "electronics")

      client.execute {
        search("electronics" / "phone").query(termQuery("speed", "4g"))
      }.await.totalHits shouldBe 1
    }
  }
}
