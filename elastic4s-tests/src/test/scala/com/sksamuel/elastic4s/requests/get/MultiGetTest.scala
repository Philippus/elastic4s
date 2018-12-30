package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar

import scala.util.Try

class MultiGetTest extends FlatSpec with MockitoSugar with DockerTests {

  Try {
    client.execute {
      deleteIndex("coldplay")
    }.await
  }

  client.execute {
    createIndex("coldplay").shards(2).mappings(
      mapping("albums").fields(
        textField("name").stored(true),
        intField("year").stored(true)
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("coldplay" / "albums") id "1" fields("name" -> "parachutes", "year" -> 2000),
      indexInto("coldplay" / "albums") id "3" fields("name" -> "x&y", "year" -> 2005),
      indexInto("coldplay" / "albums") id "5" fields("name" -> "mylo xyloto", "year" -> 2011),
      indexInto("coldplay" / "albums") id "7" fields("name" -> "ghost stories", "year" -> 2015)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get("3").from("coldplay/albums"),
        get("5") from "coldplay/albums",
        get("7") from "coldplay/albums"
      )
    ).await.result

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
        get("3").from("coldplay/albums"),
        get("711111") from "coldplay/albums"
      )
    ).await.result

    resp.size shouldBe 2
    resp.items.head.exists shouldBe true
    resp.items.last.exists shouldBe false
  }

  it should "retrieve documents by id with selected fields" in {

    val resp = client.execute(
      multiget(
        get("3") from "coldplay/albums" storedFields("name", "year"),
        get("5") from "coldplay/albums" storedFields "name"
      )
    ).await.result

    resp.size shouldBe 2
    resp.items.head.fields shouldBe Map("year" -> List(2005), "name" -> List("x&y"))
    resp.items.last.fields shouldBe Map("name" -> List("mylo xyloto"))
  }

  it should "retrieve documents by id with fetchSourceContext" in {

    val resp = client.execute(
      multiget(
        get("3") from "coldplay/albums" fetchSourceContext Seq("name", "year"),
        get("5") from "coldplay/albums" fetchSourceContext Seq("name")
      )
    ).await.result
    resp.size shouldBe 2
    resp.items.head.source shouldBe Map("year" -> 2005, "name" -> "x&y")
    resp.items.last.source shouldBe Map("name" -> "mylo xyloto")
  }
  it should "retrieve documents by id with routing spec" in {

    val resp = client.execute(
      multiget(get("3") from "coldplay/albums" routing "3")
    ).await.result

    resp.size shouldBe 1

    resp.items.head.id shouldBe "3"
    resp.items.head.exists shouldBe true
  }
}
