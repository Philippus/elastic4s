package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import scala.util.Try

class SumAggregationHttpTest extends FreeSpec with DockerTests with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    deleteIdx("sumagg")

    client.execute {
      createIndex("sumagg") mapping {
        properties(
          textField("name").fielddata(true),
          intField("age").stored(true)
        )
      }
    }.await.result.acknowledged shouldBe true


    Try {
      client.execute {
        deleteIndex("sumagg2")
      }.await
    }

    client.execute {
      createIndex("sumagg2") mapping {
        properties(
          textField("name").fielddata(true),
          intField("age").stored(true)
        )
      }
    }.await.result.acknowledged shouldBe true

    client.execute(
      bulk(
        indexInto("sumagg") fields("name" -> "clint eastwood", "age" -> "52"),
        indexInto("sumagg") fields("name" -> "eli wallach", "age" -> "72"),
        indexInto("sumagg") fields("name" -> "lee van cleef", "age" -> "62"),
        indexInto("sumagg") fields ("name" -> "nicholas cage"),
        indexInto("sumagg") fields("name" -> "sean connery", "age" -> "32"),
        indexInto("sumagg") fields("name" -> "kevin costner", "age" -> "42")
      ).refreshImmediately
    ).await
  }

  "sum aggregation" - {

    "should group by field" in {

      val resp = client.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 260.0
    }

    "should support missing" in {
      val resp = client.execute {
        search("sumagg").matchAllQuery().aggs {
          sumAgg("agg1", "age").missing("100")
        }
      }.await.result
      resp.totalHits shouldBe 6

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 360
    }

    "should support when no documents match" in {
      val resp = client.execute {
        search("sumagg2").matchAllQuery().aggs {
          sumAgg("agg1", "age").missing("100")
        }
      }.await.result
      resp.totalHits shouldBe 0

      val agg = resp.aggs.sum("agg1")
      agg.value shouldBe 0
    }
  }
}
