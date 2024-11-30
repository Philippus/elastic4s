package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, BucketAggregation}

case class HistogramAggResult(name: String, buckets: Seq[HistogramBucket]) extends BucketAggregation

object HistogramAggResult {
  def apply(name: String, data: Map[String, Any]): HistogramAggResult = HistogramAggResult(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      HistogramBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    }
  )
}

case class HistogramBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends AggBucket
