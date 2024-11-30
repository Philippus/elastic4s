package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, AggSerde, BucketAggregation}

case class GeoHashGrid(name: String, buckets: Seq[GeoHashGridBucket]) extends BucketAggregation

case class GeoHashGridBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends AggBucket

object GeoHashGrid {

  implicit object GeoHashGridAggSerde extends AggSerde[GeoHashGrid] {
    override def read(name: String, data: Map[String, Any]): GeoHashGrid = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): GeoHashGrid = GeoHashGrid(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      GeoHashGridBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}
