package com.sksamuel.elastic4s

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import com.fasterxml.jackson.databind.ObjectMapper
import ElasticDsl._
import com.sksamuel.elastic4s.source.{ObjectSource, JacksonSource}

/** @author Stephen Samuel */
class IndexTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  val mapper = new ObjectMapper()

  "an index request" should "index from jackson source when used" in {
    val json = mapper.readTree(getClass.getResourceAsStream("/json/samsung.json"))
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

  it should "index numbers" in {
    client.execute {
      index into "electronics/phone" fields Map("screensize" -> 5)
    }
    blockUntilCount(4, "electronics")

    client.execute {
      search in "electronics" / "phone" query termQuery("screensize", 5)
    }.await.getHits.getTotalHits shouldBe 1
  }

  "an index exists request" should "return true for an existing index" in {
    assert(client.exists("electronics").await.isExists)
  }

  "a delete index request" should "delete the index" in {
    assert(client.exists("electronics").await.isExists)
    client.execute {
      delete index "electronics"
    }.await
    assert(!client.exists("electronics").await.isExists)
    client.close()
  }
}
