package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.fields.GeoPointField
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.aggs.CompositeAggregation._
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class CompositeGeotileGridAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {
  Try {
    client.execute {
      deleteIndex("compositegeotilegridaggs")
    }.await
  }
  client.execute {
    createIndex("compositegeotilegridaggs") mapping {
      properties(
        textField("name").fielddata(true),
        GeoPointField("location")
      )
    }
  }.await
  client.execute(
    bulk(
      indexInto("compositegeotilegridaggs") fields ("name" -> "MacRonalds", "location"   -> "2, 3"),
      indexInto("compositegeotilegridaggs") fields ("name" -> "Henny's", "location"      -> "40, 5"),
      indexInto("compositegeotilegridaggs") fields ("name" -> "Taco Shell", "location"   -> "40, 10"),
      indexInto("compositegeotilegridaggs") fields ("name" -> "Burger Sling", "location" -> "2 , 3.1"),
      indexInto("compositegeotilegridaggs") fields ("name" -> "U-bahn", "location"       -> "29, 1"),
      indexInto("compositegeotilegridaggs") fields ("name" -> "John's hut", "location"   -> "29, 1.2")
    ).refresh(RefreshPolicy.Immediate)
  ).await
  "Composite geotile grid agg" - {
    "should return a count per geotile" in {
      val resp = client.execute {
        search("compositegeotilegridaggs").matchAllQuery().aggs {
          CompositeAggregation(
            name = "agg1",
            sources = Seq(
              GeoTileGridValueSource(
                name = "tiles",
                field = Some("location"),
                script = None,
                order = Some("ASC"),
                missingBucket = true
              )
            )
          )
        }
      }.await.result
      resp.totalHits shouldBe 6
      val agg  = resp.aggs.compositeAgg("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        CompositeAggBucket(Map("tiles" -> "7/64/53"), 2, Map.empty),
        CompositeAggBucket(Map("tiles" -> "7/65/48"), 1, Map.empty),
        CompositeAggBucket(Map("tiles" -> "7/65/63"), 2, Map.empty),
        CompositeAggBucket(Map("tiles" -> "7/67/48"), 1, Map.empty)
      )
    }
  }
}
