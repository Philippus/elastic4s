package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, BucketAggregation}

case class IpRangeAggResult(name: String, buckets: Seq[IpRangeBucket]) extends BucketAggregation

case class IpRangeBucket(key: Option[String],
                         override val docCount: Long,
                         from: Option[String],
                         to: Option[String],
                         private[elastic4s] val data: Map[String, Any]) extends AggBucket

object IpRangeAggResult {
  def apply(name: String, data: Map[String, Any]): IpRangeAggResult = IpRangeAggResult(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map.get("key").map(_.toString), map)
        }

      //keyed results
      case buckets: Map[_, _] =>
        buckets
          .asInstanceOf[Map[String, Any]]
          .map {
            case (key, values) =>
              mkBucket(Some(key), values.asInstanceOf[Map[String, Any]])
          }
          .toSeq
    }
  )

  private def mkBucket(key: Option[String], map: Map[String, Any]): IpRangeBucket =
    IpRangeBucket(
      key,
      map("doc_count").toString.toLong,
      map.get("from").map(_.toString),
      map.get("to").map(_.toString),
      map
    )
}
