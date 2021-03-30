package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class FiltersAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("filtersagg")
    }.await
  }

  client.execute {
    createIndex("filtersagg") mapping {
      mapping(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("filtersagg") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("filtersagg") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("filtersagg") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("filtersagg") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "filters agg" - {
    "should create buckets matching the query" in {

      val resp = client.execute {
        search("filtersagg").matchAllQuery().aggs {
          filtersAggregation("agg1").queries(Seq(matchQuery("name", "london"), matchQuery("name", "tower"))).subaggs {
            sumAgg("agg2", "height")
          }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.filters("agg1").aggResults.map(_.docCount).sum shouldBe 4
      resp.aggs.filters("agg1").aggResults.head.sum("agg2").value shouldBe 232
    }
  }
}
