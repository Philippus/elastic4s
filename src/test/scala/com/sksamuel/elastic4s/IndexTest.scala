package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.fasterxml.jackson.databind.ObjectMapper
import ElasticDsl._
import com.sksamuel.elastic4s.source.{ObjectSource, JacksonSource}

/** @author Stephen Samuel */
class IndexTest extends FlatSpec with MockitoSugar with ElasticSugar {

  val mapper = new ObjectMapper()

  "an index request" should "index from jackson source when used" in {
    val json = mapper.readTree(getClass.getResourceAsStream("/com/sksamuel/elastic4s/samsung.json"))
    client.execute {
      index into "electronics/phone" source JacksonSource(json)
    }
    blockUntilCount(1, "electronics")
  }

  it should "index from object source when used" in {

    case class Phone(name: String, brand: String)
    val iPhone = new Phone("iPhone", "apple")
    val one = new Phone("One", "HTC")

    client.bulk(
      index into "electronics/phone" source ObjectSource(iPhone),
      index into "electronics/phone" source ObjectSource(one)
    )
    blockUntilCount(3, "electronics")
  }

  "an index exists request" should "return true for an existing index" in {
    assert(client.sync.exists("electronics").isExists)
  }

  "a delete index request" should "delete the index" in {
    assert(client.sync.exists("electronics").isExists)
    client.sync.deleteIndex("electronics")
    assert(!client.sync.exists("electronics").isExists)
    client.close()
  }
}
