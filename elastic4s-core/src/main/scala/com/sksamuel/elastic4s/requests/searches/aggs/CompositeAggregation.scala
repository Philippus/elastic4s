package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.{AggBucket, BucketAggregation, HasAggregations}
import com.sksamuel.exts.OptionImplicits._

sealed abstract class ValueSource(val valueSourceType: String, val name: String,
                                  val field: Option[String],
                                  val script: Option[Script],
                                  val order: Option[String])

case class TermsValueSource(override val name: String,
                            override val field: Option[String] = None,
                            override val script: Option[Script] = None,
                            override val order: Option[String] = None)
  extends ValueSource("terms", name, field, script, order)

case class HistogramValueSource(override val name: String,
                                interval: Int,
                                override val field: Option[String] = None,
                                override val script: Option[Script] = None,
                                override val order: Option[String] = None)
  extends ValueSource("histogram", name, field, script, order)

case class DateHistogramValueSource(override val name: String,
                                    interval: String,
                                    override val field: Option[String] = None,
                                    override val script: Option[Script] = None,
                                    override val order: Option[String] = None,
                                    timeZone: Option[String] = None,
                                    format: Option[String] = None,
                                    missingBucket: Boolean = false)
  extends ValueSource("date_histogram", name, field, script, order)


case class CompositeAggregation(name: String,
                                sources: Seq[ValueSource] = Nil,
                                size: Option[Int] = None,
                                subaggs: Seq[AbstractAggregation] = Nil,
                                metadata: Map[String, AnyRef] = Map.empty,
                                after: Option[Map[String, AnyRef]] = None)
  extends Aggregation {

  type T = CompositeAggregation

  def sources(sources: Seq[ValueSource]): CompositeAggregation = copy(sources = sources)

  def size(size: Int): CompositeAggregation = copy(size = size.some)

  def after(after: Map[String, AnyRef]): CompositeAggregation = copy(after = after.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)

  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}

/*
 * Responses
 */
object CompositeAggregation {

  case class CompositeAggBucket(
    key: Map[String,Any],
    docCount: Long,
    override val data: Map[String, Any]
  ) extends AggBucket with HasAggregations

  case class CompositeAggregationResult(
    name: String,
    buckets: Seq[CompositeAggBucket],
    afterKey: Option[Map[String, Any]],
    private val data: Map[String, Any],
  ) extends BucketAggregation

  implicit class CompositeAggResult(aggs: HasAggregations){
    def compositeAgg(name: String) : CompositeAggregationResult = {

      val data = aggs.dataAsMap(name).asInstanceOf[Map[String,Any]]
      val buckets = data("buckets").asInstanceOf[Seq[Map[String, Any]]].map( v  =>
        CompositeAggBucket(
          key = v("key").asInstanceOf[Map[String,Any]],
          docCount = v("doc_count").toString.toLong,
          data = v
        )
      )

      CompositeAggregationResult(
        name = name,
        buckets = buckets,
        afterKey = data.get("after_key").map(_.asInstanceOf[Map[String, Any]]),
        data = data,
      )
    }
  }
}
