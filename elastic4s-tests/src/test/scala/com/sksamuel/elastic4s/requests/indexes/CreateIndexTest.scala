package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class CreateIndexTest extends AnyWordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("foo")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("cuisine")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("landscape")
    }.await
  }

  client.execute {
    createIndex("foo").mapping(
      properties(
        textField("baz").fields(
          textField("inner1"), //analyzer PatternAnalyzer,
          textField("inner2")
        )
      )
    )
  }.await

  "CreateIndex Http Request" should {
    "return ack" in {
      val resp = client.execute {
        createIndex("cuisine").mapping(
          properties(
            textField("name"),
            geopointField("location")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      resp.result.acknowledged shouldBe true
    }

    "return error object when index already exists" in {

      val resp = client.execute {
        createIndex("foo").mapping(
          properties(
            textField("b")
          )
        )
      }.await

      resp.error.`type` shouldBe "resource_already_exists_exception"
      resp.error.index shouldBe Some("foo")
    }

    "create from raw source" in {

      client.execute {
        createIndex("landscape").source(
          s"""
             {
              "mappings": {
               "properties": {
                 "content": { "type": "text" },
                 "user_name": { "type": "keyword" },
                 "tweeted_at": { "type": "date" }
               }
              }
             }
           """)
          .shards(1)
          .waitForActiveShards(1)
      }.await.result.acknowledged shouldBe true

      client.execute {
        getMapping("landscape")
      }.await.result shouldBe List(
        IndexMappings(
          "landscape",
          Map(
            "content" -> Map("type" -> "text"),
            "user_name" -> Map("type" -> "keyword"),
            "tweeted_at" -> Map("type" -> "date")
          )
        )
      )
    }
  }
}
