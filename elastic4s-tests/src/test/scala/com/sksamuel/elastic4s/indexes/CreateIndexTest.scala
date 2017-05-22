package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.PatternAnalyzer
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CreateIndexTest extends WordSpec with Matchers with DualClientTests  {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("foo")
      }.await
    }

    Try {
      execute {
        deleteIndex("cuisine")
      }.await
    }

    Try {
      execute {
        deleteIndex("landscape")
      }.await
    }

    execute {
      createIndex("foo").mappings(
        mapping("bar").fields(
          textField("baz").fields(
            textField("inner1") analyzer PatternAnalyzer,
            textField("inner2")
          )
        )
      )
    }.await
  }

  "CreateIndex Http Request" should {
    "return ack" in {
      val resp = execute {
        createIndex("cuisine").mappings(
          mapping("food").fields(
            textField("name"),
            geopointField("location")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      resp.acknowledged shouldBe true
    }

    "create from raw source" in {
      execute {
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
      }.await

      val resp = client.execute {
        getMapping("landscape").types("mountains")
      }.await

      resp.mappings.keys shouldBe Set("landscape")
      resp.mappings("landscape").keySet shouldBe Set("mountains")
    }
  }

}
