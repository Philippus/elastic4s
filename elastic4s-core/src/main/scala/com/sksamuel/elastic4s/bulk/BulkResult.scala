package com.sksamuel.elastic4s.bulk

import org.elasticsearch.action.bulk.BulkResponse

case class BulkResult(original: BulkResponse) {

  import scala.concurrent.duration._

  def failureMessage: String = original.buildFailureMessage
  def failureMessageOpt: Option[String] = Option(failureMessage)

  def took: FiniteDuration = original.getTook.millis.millis
  def hasFailures: Boolean = original.getItems.exists(_.isFailed)
  def hasSuccesses: Boolean = original.getItems.exists(!_.isFailed)
  def items: Seq[BulkItemResult] = original.getItems.map(BulkItemResult.apply)

  def failures: Seq[BulkItemResult] = items.filter(_.isFailure)
  def successes: Seq[BulkItemResult] = items.filterNot(_.isFailure)
}
