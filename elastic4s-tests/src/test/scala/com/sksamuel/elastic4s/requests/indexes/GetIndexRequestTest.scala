package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.handlers.index.{Field, Mapping}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class GetIndexRequestTest extends AnyWordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("getindextest")
    }.await
  }

  client.execute {
    createIndex("getindextest").mapping(
      properties(
        textField("a"),
        keywordField("b"),
        longField("c"),
        objectField("d")
      )
    ).settings(Map("number_of_replicas" -> 2))
  }.await
  val indexableObject = Map("foo" -> Map("bar" -> "baz"), "quux" -> "quuux")
  client.execute(indexInto("getindextest").fields("a" -> "A", "b" -> "B", "c" -> 3L, "d" -> indexableObject)).await

  "get index" should {

    "return mapping info" in {
      val resp = client.execute {
        getIndex("getindextest")
      }.await.result
      resp("getindextest").mappings shouldBe Mapping(Map(
        "a" -> Field(Some("text")),
        "b" -> Field(Some("keyword")),
        "c" -> Field(Some("long")),
        "d" -> Field(None, Some(Map("foo" -> Field(None, Some(Map("bar" -> Field(Some("text"))))), "quux" -> Field(Some("text")))))
      ))
    }

    "return settings" in {

      val resp = client.execute {
        getIndex("getindextest")
      }.await.result

      resp("getindextest").settings("index.number_of_shards") shouldBe "1"
      resp("getindextest").settings("index.number_of_replicas") shouldBe "2"
      resp("getindextest").settings("index.provided_name") shouldBe "getindextest"
    }

    "return aliases" in {

      client.execute {
        addAlias("myalias1").on("getindextest")
      }.await

      client.execute {
        addAlias("myalias2").on("getindextest")
      }.await

      val resp = client.execute {
        getIndex("getindextest")
      }.await.result

      resp("getindextest").aliases.keySet shouldBe Set("myalias1", "myalias2")
    }

    "return meta data" in {

      Try {
        client.execute {
          deleteIndex("getindexwithmeta")
        }.await
      }

      val meta = Map(
        "foo" -> "bar",
        "intvalue" -> 1,
        "mapvalue" -> Map[String, Any]("key" -> "value")
      )

      client.execute {
        createIndex("getindexwithmeta").mapping(
          properties(
            textField("a")
          )
          .meta(meta)
        )
      }.await

      val resp = client.execute {
        getIndex("getindexwithmeta")
      }.await.result

      resp("getindexwithmeta").mappings.meta shouldBe meta
    }
  }
}
