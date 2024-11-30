package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{
  AggBucket,
  AggResult,
  AggSerde,
  BucketAggregation,
  Transformable
}

case class MultiTermBucket(key: Seq[String], override val docCount: Long, private[elastic4s] val data: Map[String, Any])
    extends AggBucket
    with Transformable

case class MultiTerms(name: String, buckets: Seq[MultiTermBucket], docCountErrorUpperBound: Long, otherDocCount: Long)
    extends BucketAggregation with AggResult {
  def bucket(key: Seq[String]): MultiTermBucket            = bucketOpt(key).get
  def bucketOpt(key: Seq[String]): Option[MultiTermBucket] = buckets.find(_.key == key)
}

object MultiTerms {

  implicit object MultiTermsAggReader extends AggSerde[MultiTerms] {
    override def read(name: String, data: Map[String, Any]): MultiTerms = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): MultiTerms = MultiTerms(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map { map =>
      MultiTermBucket(
        map("key").asInstanceOf[List[String]].toSeq,
        map("doc_count").toString.toLong,
        map
      )
    },
    data("doc_count_error_upper_bound").toString.toLong,
    data("sum_other_doc_count").toString.toLong
  )
}
