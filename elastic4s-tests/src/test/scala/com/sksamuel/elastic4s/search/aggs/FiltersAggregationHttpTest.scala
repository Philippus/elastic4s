package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class FiltersAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("filtersagg")
    }.await
  }

  http.execute {
    createIndex("filtersagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("filtersagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("filtersagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("filtersagg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("filtersagg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "filters agg" - {
    "should create buckets matching the query" in {

      val resp = http.execute {
        search("filtersagg").matchAllQuery().aggs {
          filtersAggregation("agg1").queries(Seq(matchQuery("name", "london"), matchQuery("name", "tower"))).subaggs {
            sumAgg("agg2", "height")
          }
        }
      }.await.get
      resp.totalHits shouldBe 4
      resp.aggs.filters("agg1").aggResults.map(_.docCount).sum shouldBe 4
      resp.aggs.filters("agg1").aggResults.head.sum("agg2").value shouldBe 232
    }
  }
}
