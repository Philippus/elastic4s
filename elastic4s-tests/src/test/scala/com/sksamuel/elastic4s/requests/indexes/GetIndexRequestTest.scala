package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class GetIndexRequestTest extends WordSpec with Matchers with DockerTests {

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
        longField("c")
      )
    ).settings(Map("number_of_replicas" -> 2))
  }.await

  "get index" should {

    "return mapping info" in {
      val resp = client.execute {
        getIndex("getindextest")
      }.await.result
      resp("getindextest").mappings shouldBe Mapping(Map("a" -> Field("text"), "b" -> Field("keyword"), "c" -> Field("long")))
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

      client.execute {
        createIndex("getindexwithmeta").mapping(
          properties(
            textField("a")
          ).meta(Map("foo" -> "bar"))
        )
      }.await

      val resp = client.execute {
        getIndex("getindexwithmeta")
      }.await.result

      resp("getindexwithmeta").mappings.meta shouldBe Map("foo" -> "bar")
    }
  }
}
