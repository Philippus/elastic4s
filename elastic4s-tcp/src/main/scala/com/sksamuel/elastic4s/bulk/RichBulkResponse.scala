package com.sksamuel.elastic4s.bulk

import org.elasticsearch.action.bulk.BulkResponse

case class RichBulkResponse(original: BulkResponse) {

  import scala.concurrent.duration._

  def failureMessage: String = original.buildFailureMessage
  def failureMessageOpt: Option[String] = if (hasFailures) Some(failureMessage) else None

  def took: FiniteDuration = original.getTook.millis.millis
  def hasFailures: Boolean = original.getItems.exists(_.isFailed)
  def hasSuccesses: Boolean = original.getItems.exists(!_.isFailed)
  def items: Seq[RichBulkItemResponse] = original.getItems.map(RichBulkItemResponse.apply)

  def failures: Seq[RichBulkItemResponse] = items.filter(_.isFailure)
  def successes: Seq[RichBulkItemResponse] = items.filterNot(_.isFailure)
}
