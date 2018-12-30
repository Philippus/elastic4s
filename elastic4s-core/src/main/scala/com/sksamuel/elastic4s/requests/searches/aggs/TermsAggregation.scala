package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.ValueType
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.{IncludeExclude, IncludePartition}
import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.exts.StringOption

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
                            includeExclude: Option[IncludeExclude] = None,
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

  def includes: Seq[String] = includeExclude.fold(Nil: Seq[String])(_.include)
  def excludes: Seq[String] = includeExclude.fold(Nil: Seq[String])(_.exclude)

  def include(include: String): TermsAggregation = includeExclude(Seq(include), excludes)
  def exclude(exclude: String): TermsAggregation = includeExclude(includes, Seq(exclude))

  def include(includes: Seq[String]): TermsAggregation = includeExclude(includes, excludes)
  def exclude(excludes: Seq[String]): TermsAggregation = includeExclude(includes, excludes)

  def includeExclude(include: String, exclude: String): TermsAggregation =
    copy(includeExclude = IncludeExclude(StringOption(include).toSeq, StringOption(exclude).toSeq).some)

  def includeExclude(include: Iterable[String], exclude: Iterable[String]): TermsAggregation =
    copy(includeExclude = IncludeExclude(include.toSeq, exclude.toSeq).some)

  def includePartition(partition: Int, numPartitions: Int): TermsAggregation =
    copy(includePartition = IncludePartition(partition, numPartitions).some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}

case class TermsOrder(name: String, asc: Boolean = true)
