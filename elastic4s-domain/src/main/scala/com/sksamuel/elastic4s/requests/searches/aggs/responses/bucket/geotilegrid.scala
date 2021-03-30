package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, AggSerde, BucketAggregation}

case class GeoTileGrid(name: String, buckets: Seq[GeoTileGridBucket]) extends BucketAggregation

case class GeoTileGridBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends AggBucket

object GeoTileGrid {

  implicit object GeoTileGridAggSerde extends AggSerde[GeoTileGrid] {
    override def read(name: String, data: Map[String, Any]): GeoTileGrid = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): GeoTileGrid = GeoTileGrid(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      GeoTileGridBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}

