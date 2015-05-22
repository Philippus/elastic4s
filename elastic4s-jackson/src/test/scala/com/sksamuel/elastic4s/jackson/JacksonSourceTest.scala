package com.sksamuel.elastic4s.jackson

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticSugar
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/** @author Stephen Samuel */
class JacksonSourceTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    create.index("electronics").mappings("phone" ttl true)
  }.await

  "an index request" should "index from jackson source when used" in {
    val json = JacksonJson.mapper.readTree(getClass.getResourceAsStream("/json/samsung.json"))
    client.execute {
      index into "electronics/phone" doc JacksonSource(json)
    }
    blockUntilCount(1, "electronics")
  }

  it should "index from object source when used" in {

    case class Phone(name: String, brand: String)
    val iPhone = new Phone("iPhone", "apple")
    val one = new Phone("One", "HTC")

    client.execute(
      bulk(
        index into "electronics/phone" doc ObjectSource(iPhone),
        index into "electronics/phone" doc ObjectSource(one)
      )
    )
    blockUntilCount(3, "electronics")
  }
}
