package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, AggResult, AggSerde, BucketAggregation, Transformable}

case class TermBucket(key: String, override val docCount: Long, private[elastic4s] val data: Map[String, Any])
  extends AggBucket
    with Transformable

case class Terms(name: String, buckets: Seq[TermBucket], docCountErrorUpperBound: Long, otherDocCount: Long)
  extends BucketAggregation with AggResult {
  def bucket(key: String): TermBucket = bucketOpt(key).get
  def bucketOpt(key: String): Option[TermBucket] = buckets.find(_.key == key)
}

object Terms {

  implicit object TermsAggReader extends AggSerde[Terms] {
    override def read(name: String, data: Map[String, Any]): Terms = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): Terms = Terms(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      TermBucket(
        map("key").toString,
        map("doc_count").toString.toLong,
        map
      )
    },
    data("doc_count_error_upper_bound").toString.toLong,
    data("sum_other_doc_count").toString.toLong
  )
}

