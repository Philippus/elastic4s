package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.{GeoTileGrid, GeoTileGridBucket}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class GeoTileGridAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  val indexName = "geotile_grid_agg"

  Try {
    client.execute {
      deleteIndex(indexName)
    }.await
  }

  client.execute {
    createIndex(indexName).mapping(
      properties(geopointField("location"))
    )
  }.await

  client.execute(
    bulk(
      indexInto(indexName).fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto(indexName).fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto(indexName).fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto(indexName).fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto(indexName).fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto(indexName).fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await

  "geohashgrid agg" - {
    "should return expected key values" in {
      val resp = client.execute {
        search(indexName).matchAllQuery().aggs {
          geoTileGridAggregation("geo_grid")
            .precision(2)
            .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.result[GeoTileGrid]("geo_grid")

      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoTileGridBucket("2/2/1", 6, Map.empty)
      )
    }
  }
}
