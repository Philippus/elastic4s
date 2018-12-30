package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GeoBoundsAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("geoboundsagg")
    }.await
  }

  client.execute {
    createIndex("geoboundsagg") mappings {
      mapping("doc") fields geopointField("location")
    }
  }.await

  // based on the examples from Geo Distance Aggregation docs
  client.execute(
    bulk(
      indexInto("geoboundsagg/doc").fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto("geoboundsagg/doc").fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto("geoboundsagg/doc").fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto("geoboundsagg/doc").fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto("geoboundsagg/doc").fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto("geoboundsagg/doc").fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await

  "geobounds agg" - {
    "should return expected region corners" in {
      val resp = client.execute {
        search("geoboundsagg").matchAllQuery().aggs {
          geoBoundsAggregation("museums_region")
              .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoBounds("museums_region")
      agg.topLeft.get.lat      shouldBe 52.37 +- 0.01
      agg.topLeft.get.long     shouldBe  2.32 +- 0.01
      agg.bottomRight.get.lat  shouldBe 48.85 +- 0.01
      agg.bottomRight.get.long shouldBe  4.91 +- 0.01
    }

    "should return empty aggregation in case of no documents are returned" in {
      val resp = client.execute {
        search("geoboundsagg").query(termQuery("name", "Guggenheim")).aggs {
          geoBoundsAggregation("museums_region")
            .field("location")
        }
      }.await.result

      resp.totalHits shouldBe 0

      val agg = resp.aggs.geoBounds("museums_region")
      (agg.topLeft, agg.bottomRight) shouldBe (None, None)
    }

  }
}
