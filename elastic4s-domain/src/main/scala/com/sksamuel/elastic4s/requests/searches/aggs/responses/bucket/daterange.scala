package com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket

import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggBucket, AggSerde, BucketAggregation}

case class KeyedDateRangeAggResult(name: String, buckets: Map[String, DateRangeBucket]) extends BucketAggregation

object KeyedDateRangeAggResult {
  // type clash with `buckets` on apply method
  private[elastic4s] def fromData(name: String, data: Map[String, Any]): KeyedDateRangeAggResult =
    KeyedDateRangeAggResult(
      name,
      data("buckets").asInstanceOf[Map[String, Map[String, Any]]].mapValues(DateRangeBucket(_)).toMap
    )
}

case class DateRangeBucket(
    from: Option[String],
    fromAsString: Option[String],
    to: Option[String],
    toAsString: Option[String],
    key: Option[String],
    override val docCount: Long,
    private[elastic4s] val data: Map[String, Any]
) extends AggBucket

object DateRangeBucket {
  private[elastic4s] def apply(map: Map[String, Any]): DateRangeBucket = DateRangeBucket(
    map.get("from").map(_.toString),
    map.get("from_as_string").map(_.toString),
    map.get("to").map(_.toString),
    map.get("to_as_string").map(_.toString),
    map.get("key").map(_.toString),
    map("doc_count").toString.toLong,
    map
  )
}

case class DateRange(name: String, buckets: Seq[DateRangeBucket]) extends BucketAggregation

object DateRange {

  implicit object DateRangeAggSerde extends AggSerde[DateRange] {
    override def read(name: String, data: Map[String, Any]): DateRange = apply(name, data)
  }

  private[elastic4s] def apply(name: String, data: Map[String, Any]): DateRange = DateRange(
    name,
    data("buckets").asInstanceOf[Seq[Map[String, Any]]].map(DateRangeBucket(_))
  )
}
