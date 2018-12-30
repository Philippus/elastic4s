package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.{GeoDistanceBucket, GeoPoint}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class GeoDistanceAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("geodistanceagg")
    }.await
  }

  client.execute {
    createIndex("geodistanceagg") mappings {
      mapping("doc") fields geopointField("location")
    }
  }.await

  // based on the examples from Geo Distance Aggregation docs
  client.execute(
    bulk(
      indexInto("geodistanceagg/doc").fields("location" -> "52.374081,4.912350", "name" -> "NEMO Science Museum"),
      indexInto("geodistanceagg/doc").fields("location" -> "52.369219,4.901618", "name" -> "Museum Het Rembrandthuis"),
      indexInto("geodistanceagg/doc").fields("location" -> "52.371667,4.914722", "name" -> "Nederlands Scheepvaartmuseum"),
      indexInto("geodistanceagg/doc").fields("location" -> "51.222900,4.405200", "name" -> "Letterenhuis"),
      indexInto("geodistanceagg/doc").fields("location" -> "48.861111,2.336389", "name" -> "Musée du Louvre"),
      indexInto("geodistanceagg/doc").fields("location" -> "48.860000,2.327000", "name" -> "Musée d'Orsay")
    ).refreshImmediately
  ).await

  "geodistance agg" - {
    "should return expected buckets" in {
      val resp = client.execute {
        search("geodistanceagg").matchAllQuery().aggs {
          geoDistanceAggregation("rings_around_amsterdam")
              .origin(GeoPoint(52.3760, 4.894))
              .field("location")
              .unboundedTo(to = 100000.0)
              .range(from = 100000.0, to = 300000.0)
              .unboundedFrom(from = 300000.0)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoDistance("rings_around_amsterdam")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoDistanceBucket("*-100000.0", 3, Some(0.0), Some(100000.0), Map.empty),
        GeoDistanceBucket("100000.0-300000.0", 1, Some(100000.0), Some(300000.0), Map.empty),
        GeoDistanceBucket("300000.0-*", 2, Some(300000.0), None, Map.empty)
      )
    }

    "should return expected buckets with specified unit" in {
      val resp = client.execute {
        search("geodistanceagg").matchAllQuery().aggs {
          geoDistanceAggregation("rings_around_amsterdam")
              .origin(GeoPoint(52.3760, 4.894))
              .field("location")
              .unit(DistanceUnit.KILOMETERS)
              .unboundedTo(to = 100.0)
              .range(from = 100.0, to = 300.0)
              .unboundedFrom(from = 300.0)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoDistance("rings_around_amsterdam")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoDistanceBucket("*-100.0", 3, Some(0.0), Some(100.0), Map.empty),
        GeoDistanceBucket("100.0-300.0", 1, Some(100.0), Some(300.0), Map.empty),
        GeoDistanceBucket("300.0-*", 2, Some(300.0), None, Map.empty)
      )
    }

    "should return expected keyed buckets" in {
      val resp = client.execute {
        search("geodistanceagg").matchAllQuery().aggs {
          geoDistanceAggregation("rings_around_amsterdam")
              .origin(GeoPoint(52.3760, 4.894))
              .field("location")
              .unboundedTo(to = 100000.0)
              .range(from = 100000.0, to = 300000.0)
              .unboundedFrom(from = 300000.0)
              .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.geoDistance("rings_around_amsterdam")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        GeoDistanceBucket("*-100000.0", 3, Some(0.0), Some(100000.0), Map.empty),
        GeoDistanceBucket("100000.0-300000.0", 1, Some(100000.0), Some(300000.0), Map.empty),
        GeoDistanceBucket("300000.0-*", 2, Some(300000.0), None, Map.empty)
      )
    }
  }
}
