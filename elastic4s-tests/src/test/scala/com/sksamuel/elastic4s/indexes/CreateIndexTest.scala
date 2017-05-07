package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.analyzers.PatternAnalyzer
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class CreateIndexTest extends WordSpec with Matchers with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
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

    "support multiple types" in {

      execute {
        createIndex("geography").mappings(
          mapping("shire").fields(
            textField("name")
          ),
          mapping("mountain").fields(
            textField("range")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      val resp = client.execute {
        getMapping("geography").types("shire", "mountain")
      }.await

      resp.mappings.keys shouldBe Set("geography")
      resp.mappings("geography").keySet shouldBe Set("shire", "mountain")
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
