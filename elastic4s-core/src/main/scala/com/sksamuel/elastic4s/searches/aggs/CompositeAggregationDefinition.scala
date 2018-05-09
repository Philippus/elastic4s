package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

sealed abstract class ValueSource(val valueSourceType: String, val name: String,
                                  val field: Option[String],
                                  val script: Option[ScriptDefinition],
                                  val order: Option[String])

case class TermsValueSource(override val name: String,
                            override val field: Option[String] = None,
                            override val script: Option[ScriptDefinition] = None,
                            override val order: Option[String] = None)
  extends ValueSource("terms", name, field, script, order)

case class HistogramValueSource(override val name: String,
                                interval: Int,
                                override val field: Option[String] = None,
                                override val script: Option[ScriptDefinition] = None,
                                override val order: Option[String] = None)
  extends ValueSource("histogram", name, field, script, order)

case class DateHistogramValueSource(override val name: String,
                                    interval: String,
                                    override val field: Option[String] = None,
                                    override val script: Option[ScriptDefinition] = None,
                                    override val order: Option[String] = None,
                                    timeZone: Option[String] = None)
  extends ValueSource("date_histogram", name, field, script, order)


case class CompositeAggregationDefinition(name: String,
                                          sources: Seq[ValueSource] = Nil,
                                          size: Option[Int] = None,
                                          subaggs: Seq[AbstractAggregation] = Nil,
                                          metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = CompositeAggregationDefinition

  def sources(sources: Seq[ValueSource]): CompositeAggregationDefinition = copy(sources = sources)

  def size(size: Int): CompositeAggregationDefinition = copy(size = size.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)

  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
