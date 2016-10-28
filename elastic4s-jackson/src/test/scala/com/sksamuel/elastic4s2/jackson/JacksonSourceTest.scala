package com.sksamuel.elastic4s2.jackson

import com.sksamuel.elastic4s2.ElasticDsl._
import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.scalatest.mock.MockitoSugar
import org.scalatest.{WordSpec, FlatSpec, Matchers}

/** @author Stephen Samuel */
class JacksonSourceTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    create.index("jacksonsource").mappings("phone" ttl true)
  }.await

  "an index request" should {
    "index from jackson source when used" in {
      val json = JacksonJson.mapper.readTree(getClass.getResourceAsStream("/json/samsung.json"))
      client.execute {
        index into "jacksonsource/phone" doc JacksonSource(json)
      }
      blockUntilCount(1, "jacksonsource")
    }
    "index from object source when used" in {

      case class Phone(name: String, brand: String)
      val iPhone = new Phone("iPhone", "apple")
      val one = new Phone("One", "HTC")

      client.execute(
        bulk(
          index into "jacksonsource/phone" doc ObjectSource(iPhone),
          index into "jacksonsource/phone" doc ObjectSource(one)
        )
      )
      blockUntilCount(3, "jacksonsource")
    }
  }
}
