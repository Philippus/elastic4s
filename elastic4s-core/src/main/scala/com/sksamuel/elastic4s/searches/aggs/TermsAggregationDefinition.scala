package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ValueType
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.{IncludeExclude, IncludePartition}
import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.exts.StringOption

sealed trait SubAggCollectionMode
object SubAggCollectionMode {
  case object DepthFirst extends SubAggCollectionMode
  case object BreadthFirst extends SubAggCollectionMode
}

case class TermsAggregationDefinition(name: String,
                                      field: Option[String] = None,
                                      script: Option[ScriptDefinition] = None,
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
  extends AggregationDefinition {

  type T = TermsAggregationDefinition

  def field(field: String): TermsAggregationDefinition = copy(field = field.some)
  def script(script: ScriptDefinition): TermsAggregationDefinition = copy(script = script.some)
  def missing(missing: AnyRef): TermsAggregationDefinition = copy(missing = missing.some)
  def size(size: Int): TermsAggregationDefinition = copy(size = size.some)
  def minDocCount(min: Long): TermsAggregationDefinition = copy(minDocCount = min.some)
  def showTermDocCountError(showError: Boolean): TermsAggregationDefinition = copy(showTermDocCountError = showError.some)
  def valueType(valueType: ValueType): TermsAggregationDefinition = copy(valueType = valueType.some)
  def executionHint(hint: String): TermsAggregationDefinition = copy(executionHint = hint.some)
  def shardMinDocCount(min: Long): TermsAggregationDefinition = copy(shardMinDocCount = min.some)
  def collectMode(mode: SubAggCollectionMode): TermsAggregationDefinition = copy(collectMode = mode.some)

  def order(orders: Iterable[TermsOrder]): TermsAggregationDefinition = copy(orders = orders.toSeq)
  def order(firstOrder: TermsOrder, restOrder: TermsOrder*): TermsAggregationDefinition = copy(orders = firstOrder +: restOrder)

  def shardSize(shardSize: Int): TermsAggregationDefinition = copy(shardSize = shardSize.some)

  def includes: Seq[String] = includeExclude.fold(Nil: Seq[String])(_.include)
  def excludes: Seq[String] = includeExclude.fold(Nil: Seq[String])(_.exclude)

  def include(include: String): TermsAggregationDefinition = includeExclude(Seq(include), excludes)
  def exclude(exclude: String): TermsAggregationDefinition = includeExclude(includes, Seq(exclude))

  def include(includes: Seq[String]): TermsAggregationDefinition = includeExclude(includes, excludes)
  def exclude(excludes: Seq[String]): TermsAggregationDefinition = includeExclude(includes, excludes)

  def includeExclude(include: String, exclude: String): TermsAggregationDefinition =
    copy(includeExclude = IncludeExclude(StringOption(include).toSeq, StringOption(exclude).toSeq).some)

  def includeExclude(include: Iterable[String], exclude: Iterable[String]): TermsAggregationDefinition = {
    copy(includeExclude = IncludeExclude(include.toSeq, exclude.toSeq).some)
  }

  def includePartition(partition: Int, numPartitions: Int): TermsAggregationDefinition = {
    copy(includePartition = IncludePartition(partition, numPartitions).some)
  }

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}

case class TermsOrder(name: String, asc: Boolean = true)
