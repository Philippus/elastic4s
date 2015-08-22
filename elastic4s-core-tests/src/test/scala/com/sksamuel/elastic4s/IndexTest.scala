package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.Indexable
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

/** @author Stephen Samuel */
class IndexTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    create.index("electronics").mappings("phone" ttl true)
  }.await

  "an index request" should {
    "index numbers" in {
      client.execute {
        index into "electronics/phone" fields Map("screensize" -> 5)
      }
      blockUntilCount(1, "electronics")

      client.execute {
        search in "electronics" / "phone" query termQuery("screensize", 5)
      }.await.getHits.getTotalHits shouldBe 1
    }
    "index from indexable typeclass" in {

      case class Phone(name: String, speed: String)
      implicit object PhoneIndexable extends Indexable[Phone] {
        override def json(t: Phone): String = s"""{ "name" : "${t.name}", "speed" : "${t.speed}" }"""
      }
      val phone = Phone("nokia blabble", "4g")

      client.execute {
        index into "electronics/phone" source phone
      }
      blockUntilCount(2, "electronics")

      client.execute {
        search in "electronics" / "phone" query termQuery("speed", "4g")
      }.await.getHits.getTotalHits shouldBe 1
    }
    "expire a document once the TTL has passed" in {
      import scala.concurrent.duration._
      client.execute {
        index into "electronics/phone" fields "vender" -> "blackberry" ttl 1.seconds
      }
      blockUntilCount(3, "electronics")
      blockUntilCount(2, "electronics")
    }
  }
  "an index exists request" should {
    "return true for an existing index" in {
      client.execute {
        index exists "electronics"
      }.await.isExists shouldBe true
    }
  }

  "a delete index request" should {
    "delete the index" in {
      client.execute {
        index exists "electronics"
      }.await.isExists shouldBe true
      client.execute {
        delete index "electronics"
      }.await
      client.execute {
        index exists "electronics"
      }.await.isExists shouldBe false
    }
  }
}
