package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, BucketAggregation}

case class GeoDistanceAggResult(name: String, buckets: Seq[GeoDistanceBucket]) extends BucketAggregation

case class GeoDistanceBucket(key: String,
                             override val docCount: Long,
                             from: Option[Double],
                             to: Option[Double],
                             private[elastic4s] val data: Map[String, Any]) extends AggBucket

object GeoDistanceAggResult {
  def apply(name: String, data: Map[String, Any]): GeoDistanceAggResult = GeoDistanceAggResult(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map("key").toString, map)
        }

      //keyed results
      case buckets: Map[_, _] =>
        buckets
          .asInstanceOf[Map[String, Any]]
          .map {
            case (key, values) =>
              mkBucket(key, values.asInstanceOf[Map[String, Any]])
          }
          .toSeq
    }
  )

  private def mkBucket(key: String, map: Map[String, Any]): GeoDistanceBucket =
    GeoDistanceBucket(
      key,
      map("doc_count").toString.toLong,
      map.get("from").map(_.toString.toDouble),
      map.get("to").map(_.toString.toDouble),
      map
    )
}
