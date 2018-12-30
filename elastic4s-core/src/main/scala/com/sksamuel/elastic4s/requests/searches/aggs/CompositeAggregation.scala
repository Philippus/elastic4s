package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
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
                                    timeZone: Option[String] = None)
  extends ValueSource("date_histogram", name, field, script, order)


case class CompositeAggregation(name: String,
                                sources: Seq[ValueSource] = Nil,
                                size: Option[Int] = None,
                                subaggs: Seq[AbstractAggregation] = Nil,
                                metadata: Map[String, AnyRef] = Map.empty)
  extends Aggregation {

  type T = CompositeAggregation

  def sources(sources: Seq[ValueSource]): CompositeAggregation = copy(sources = sources)

  def size(size: Int): CompositeAggregation = copy(size = size.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)

  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
