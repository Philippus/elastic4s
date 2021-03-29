package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.ValueType
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.IncludePartition
import com.sksamuel.exts.OptionImplicits._

sealed trait SubAggCollectionMode
object SubAggCollectionMode {
  case object DepthFirst   extends SubAggCollectionMode
  case object BreadthFirst extends SubAggCollectionMode
}

case class TermsAggregation(name: String,
                            field: Option[String] = None,
                            script: Option[Script] = None,
                            missing: Option[AnyRef] = None,
                            size: Option[Int] = None,
                            minDocCount: Option[Long] = None,
                            showTermDocCountError: Option[Boolean] = None,
                            valueType: Option[ValueType] = None,
                            executionHint: Option[String] = None,
                            shardMinDocCount: Option[Long] = None,
                            collectMode: Option[SubAggCollectionMode] = None,
                            orders: Seq[TermsOrder] = Nil,
                            shardSize: Option[Int] = None,
                            includeExactValues: Seq[String] = Nil,
                            includeRegex: Option[String] = None,
                            excludeExactValues: Seq[String] = Nil,
                            excludeRegex: Option[String] = None,
                            includePartition: Option[IncludePartition] = None,
                            subaggs: Seq[AbstractAggregation] = Nil,
                            metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = TermsAggregation

  def field(field: String): TermsAggregation     = copy(field = field.some)
  def script(script: Script): TermsAggregation   = copy(script = script.some)
  def missing(missing: AnyRef): TermsAggregation = copy(missing = missing.some)
  def size(size: Int): TermsAggregation          = copy(size = size.some)
  def minDocCount(min: Long): TermsAggregation   = copy(minDocCount = min.some)
  def showTermDocCountError(showError: Boolean): TermsAggregation =
    copy(showTermDocCountError = showError.some)
  def valueType(valueType: ValueType): TermsAggregation         = copy(valueType = valueType.some)
  def executionHint(hint: String): TermsAggregation             = copy(executionHint = hint.some)
  def shardMinDocCount(min: Long): TermsAggregation             = copy(shardMinDocCount = min.some)
  def collectMode(mode: SubAggCollectionMode): TermsAggregation = copy(collectMode = mode.some)

  def order(orders: Iterable[TermsOrder]): TermsAggregation = copy(orders = orders.toSeq)
  def order(firstOrder: TermsOrder, restOrder: TermsOrder*): TermsAggregation =
    copy(orders = firstOrder +: restOrder)

  def shardSize(shardSize: Int): TermsAggregation = copy(shardSize = shardSize.some)

  def includeExactValues(head: String, tail: String*): TermsAggregation = copy(includeExactValues = head +: tail)
  def excludeExactValues(head: String, tail: String*): TermsAggregation = copy(excludeExactValues = head +: tail)

  def includeExactValues(includes: Seq[String]): TermsAggregation = copy(includeExactValues = includes)
  def excludeExactValues(excludes: Seq[String]): TermsAggregation = copy(excludeExactValues = excludes)

  def includeRegex(regex: String): TermsAggregation = copy(includeRegex = Some(regex))
  def excludeRegex(regex: String): TermsAggregation = copy(excludeRegex = Some(regex))

  def includePartition(partition: Int, numPartitions: Int): TermsAggregation =
    copy(includePartition = IncludePartition(partition, numPartitions).some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}

case class TermsOrder(name: String, asc: Boolean = true)
