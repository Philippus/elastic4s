package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar
import scala.collection.JavaConverters._

class MultiGetTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    createIndex("coldplay").shards(2).mappings(
      mapping("albums").fields(
        textField("name").stored(true),
        intField("year").stored(true)
      )
    )
  }.await

  def albumIndexRequest(id: Long, name: String, year: Int, revision: Long) =
    indexInto("coldplay/albums")
      .fields("name" -> name, "year" -> year)
      .id(id)
      .version(revision)

  client.execute(
    bulk(
      albumIndexRequest(1, "parachutes", 2000, 5),
      albumIndexRequest(3, "x&y", 2005, 4),
      albumIndexRequest(5, "mylo xyloto", 2011, 2),
      albumIndexRequest(7, "ghost stories", 2005, 1)
    )
  ).await

  refresh("coldplay")
  blockUntilCount(4, "coldplay")

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get(3).from("coldplay/albums"),
        get(5) from "coldplay/albums",
        get(7) from "coldplay/albums"
      )
    ).await

    resp.size shouldBe 3

    resp.items.head.id shouldBe "3"
    resp.items.head.exists shouldBe true

    resp.items(1).id shouldBe "5"
    resp.items(1).exists shouldBe true

    resp.items.last.id shouldBe "7"
    resp.items.last.exists shouldBe true
  }

  it should "set exists=false for missing documents" in {

    val resp = client.execute(
      multiget(
        get(3).from("coldplay/albums"),
        get(711111) from "coldplay/albums"
      )
    ).await

    resp.size shouldBe 2
    resp.items.head.exists shouldBe true
    resp.items.last.exists shouldBe false
  }

  it should "retrieve documents by id with selected fields" in {

    val resp = client.execute(
      multiget(
        get(3) from "coldplay/albums" storedFields("name", "year"),
        get(5) from "coldplay/albums" storedFields "name"
      )
    ).await

    resp.size shouldBe 2
    resp.items.head.response.fields.keySet shouldBe Set("name", "year")
    resp.items.last.response.fields.keySet shouldBe Set("name")
  }

  it should "retrieve documents by id with fetchSourceContext" in {

    val resp = client.execute(
      multiget(
        get(3) from "coldplay/albums" fetchSourceContext Seq("name", "year"),
        get(5) from "coldplay/albums" storedFields "name" fetchSourceContext Seq("name")
      )
    ).await
    assert(2 === resp.items.size)
    assert(resp.items.head.original.getResponse.getSource.asScala.keySet === Set("name", "year"))
    assert(resp.items.last.original.getResponse.getSource.asScala.keySet === Set("name"))
  }

  it should "retrieve documents by id and version" in {

    val resp = client.execute(
      multiget(
        get(3) from "coldplay/albums" version 1,
        get(3) from "coldplay/albums" version 4
      )
    ).await

    resp.size shouldBe 2
    resp.items.head.failed shouldBe true
    resp.items.last.exists shouldBe true
    resp.items.last.response.version shouldBe 4
  }
}
