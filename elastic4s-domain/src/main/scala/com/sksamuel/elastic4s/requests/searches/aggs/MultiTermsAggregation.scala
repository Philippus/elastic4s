package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class MultiTermsAggregation(
    name: String,
    terms: Seq[MultiTermsAggregation.Term] = List.empty,
    size: Option[Int] = None,
    minDocCount: Option[Long] = None,
    script: Option[Script] = None,
    orders: Seq[TermsOrder] = Nil,
    subaggs: Seq[AbstractAggregation] = Nil,
    metadata: Map[String, AnyRef] = Map.empty
) extends Aggregation {

  type T = MultiTermsAggregation

  def terms(terms: Iterable[MultiTermsAggregation.Term]): MultiTermsAggregation                         = copy(terms = terms.toSeq)
  def terms(head: MultiTermsAggregation.Term, tail: MultiTermsAggregation.Term*): MultiTermsAggregation =
    copy(terms = head +: tail)
  def script(script: Script): MultiTermsAggregation                                                     = copy(script = script.some)
  def size(size: Int): MultiTermsAggregation                                                            = copy(size = size.some)
  def minDocCount(min: Long): MultiTermsAggregation                                                     = copy(minDocCount = min.some)

  def order(orders: Iterable[TermsOrder]): MultiTermsAggregation                   = copy(orders = orders.toSeq)
  def order(firstOrder: TermsOrder, restOrder: TermsOrder*): MultiTermsAggregation =
    copy(orders = firstOrder +: restOrder)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}

object MultiTermsAggregation {

  case class Term(
      field: Option[String] = None,
      missing: Option[AnyRef] = None,
      showTermDocCountError: Option[Boolean] = None,
      shardMinDocCount: Option[Long] = None,
      collectMode: Option[SubAggCollectionMode] = None,
      shardSize: Option[Int] = None
  ) {

    def field(field: String): Term                      = copy(field = field.some)
    def missing(missing: AnyRef): Term                  = copy(missing = missing.some)
    def showTermDocCountError(showError: Boolean): Term =
      copy(showTermDocCountError = showError.some)
    def shardMinDocCount(min: Long): Term               = copy(shardMinDocCount = min.some)
    def collectMode(mode: SubAggCollectionMode): Term   = copy(collectMode = mode.some)

    def shardSize(shardSize: Int): Term = copy(shardSize = shardSize.some)
  }
}
