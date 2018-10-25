package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.http.search.GeoHashGridBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GeoHashGridAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("geohashgrid")
    }.await
  }

  client.execute {
    createIndex("geohashgrid") mappings {
      mapping("doc") fields geopointField("location")
    }
  }.await

  // based on the examples from Geo Distance Aggregation docs
  client.execute(
    bulk(
      indexInto("geohashgrid/doc").fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto("geohashgrid/doc").fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto("geohashgrid/doc").fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto("geohashgrid/doc").fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto("geohashgrid/doc").fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto("geohashgrid/doc").fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await

  "geohashgrid agg" - {

    "should return expected keyed buckets" in {
      val resp = client.execute {
        search("geohashgrid").matchAllQuery().aggs {
          geoHashGridAggregation("global_geohashes")
              .field("location").precision(2)
        }
      }.await.result

      val agg = resp.aggs.geoHashGrid("global_geohashes")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoHashGridBucket("u1", 4, Map.empty),
        GeoHashGridBucket("u0", 2, Map.empty)
      )
    }
  }
}
