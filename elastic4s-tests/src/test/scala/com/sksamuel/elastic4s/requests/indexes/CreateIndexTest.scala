package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.analyzers.PatternAnalyzer
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CreateIndexTest extends WordSpec with Matchers with DockerTests {

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
    createIndex("foo").mappings(
      mapping("bar").fields(
        textField("baz").fields(
          textField("inner1") analyzer PatternAnalyzer,
          textField("inner2")
        )
      )
    )
  }.await

  "CreateIndex Http Request" should {
    "return ack" in {
      val resp = client.execute {
        createIndex("cuisine").mappings(
          mapping("food").fields(
            textField("name"),
            geopointField("location")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      resp.result.acknowledged shouldBe true
    }

    "return error object when index already exists" in {

      val resp = client.execute {
        createIndex("foo").mappings(
          mapping("a").fields(
            textField("b")
          )
        )
      }.await

      resp.error.`type` shouldBe "resource_already_exists_exception"
      resp.error.index shouldBe Some("foo")
    }

    "create from raw source" in {

      client.execute {
        createIndex("landscape").source(s"""
             {
              "mappings": {
                "mountains": {
                  "properties": {
                    "name": {
                      "type": "text"
                    }
                  }
                }
              }
             }
           """).shards(1).waitForActiveShards(1)
      }.await.result.acknowledged shouldBe true
    }
  }

}
