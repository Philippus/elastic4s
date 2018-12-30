package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.GeoHashGridBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GeoHashGridAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("geohashgridagg")
    }.await
  }

  client.execute {
    createIndex("geohashgridagg") mappings {
      mapping("doc") fields geopointField("location")
    }
  }.await

  client.execute(
    bulk(
      indexInto("geohashgridagg/doc").fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto("geohashgridagg/doc").fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto("geohashgridagg/doc").fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto("geohashgridagg/doc").fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto("geohashgridagg/doc").fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto("geohashgridagg/doc").fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await


  "geohashgrid agg" - {
    "should return expected key values" in {
      val resp = client.execute {
        search("geohashgridagg").matchAllQuery().aggs {
          geoHashGridAggregation("geo_grid")
            .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoHashGrid("geo_grid")

      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoHashGridBucket("u173z", 3, Map.empty),
        GeoHashGridBucket("u155k", 1, Map.empty),
        GeoHashGridBucket("u09tv", 1, Map.empty),
        GeoHashGridBucket("u09tu", 1, Map.empty)
      )
    }
  }
}
