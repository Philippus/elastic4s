package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GeoCentroidAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("geocentroidagg")
    }.await
  }

  client.execute {
    createIndex("geocentroidagg") mappings {
      mapping("doc") fields geopointField("location")
    }
  }.await

  // based on the examples from Geo Distance Aggregation docs
  client.execute(
    bulk(
      indexInto("geocentroidagg/doc").fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto("geocentroidagg/doc").fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto("geocentroidagg/doc").fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto("geocentroidagg/doc").fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto("geocentroidagg/doc").fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto("geocentroidagg/doc").fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await

  "geocentroid agg" - {
    "should return expected region center" in {
      val resp = client.execute {
        search("geocentroidagg").matchAllQuery().aggs {
          geoCentroidAggregation("museums_center")
              .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoCentroid("museums_center")
      agg.centroid.get.lat      shouldBe 51.00 +- 0.01
      agg.centroid.get.long     shouldBe  3.96 +- 0.01
      agg.count                 shouldBe 6
    }

    "should return empty aggregation in case of no documents are returned" in {
      val resp = client.execute {
        search("geocentroidagg").query(termQuery("name", "Guggenheim")).aggs {
          geoCentroidAggregation("museums_center")
            .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 0

      val agg = resp.aggs.geoCentroid("museums_center")
      (agg.centroid, agg.count) shouldBe (None, 0)
    }

  }
}
