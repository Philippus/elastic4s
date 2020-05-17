package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, AggResult, AggSerde, BucketAggregation}

case class DateHistogram(name: String, buckets: Seq[DateHistogramBucket]) extends BucketAggregation with AggResult

object DateHistogram {

  implicit object DateHistogramAggReader extends AggSerde[DateHistogram] {
    override def read(name: String, data: Map[String, Any]): DateHistogram = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): DateHistogram = DateHistogram(
    name,
    data("buckets") match {
      case buckets: Seq[_] =>
        buckets.asInstanceOf[Seq[Map[String, Any]]].map { map =>
          mkBucket(map("key_as_string").toString, map)
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

  private def mkBucket(key: String, map: Map[String, Any]): DateHistogramBucket =
    DateHistogramBucket(
      key,
      map("key").toString.toLong,
      map("doc_count").toString.toLong,
      map
    )
}

case class DateHistogramBucket(date: String,
                               timestamp: Long,
                               override val docCount: Long,
                               private[elastic4s] val data: Map[String, Any]) extends AggBucket

