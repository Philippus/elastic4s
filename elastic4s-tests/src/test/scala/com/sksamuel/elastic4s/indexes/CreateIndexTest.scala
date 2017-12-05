package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.PatternAnalyzer
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CreateIndexTest extends WordSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("foo")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("cuisine")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("landscape")
    }.await
  }

  http.execute {
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
      val resp = http.execute {
        createIndex("cuisine").mappings(
          mapping("food").fields(
            textField("name"),
            geopointField("location")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      resp.right.get.result.acknowledged shouldBe true
    }

    "return error object when index already exists" in {

      val resp = http.execute {
        createIndex("foo").mappings(
          mapping("a").fields(
            textField("b")
          )
        )
      }.await

      resp.left.get.error.`type` shouldBe "resource_already_exists_exception"
      resp.left.get.error.index shouldBe Some("foo")
    }

    "create from raw source" in {

      http.execute {
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
      }.await.right.get.result.acknowledged shouldBe true
    }
  }

}
