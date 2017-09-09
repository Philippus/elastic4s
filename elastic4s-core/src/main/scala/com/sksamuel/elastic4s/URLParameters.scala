package com.sksamuel.elastic4s

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

import scala.concurrent.duration.FiniteDuration


case class URLParameters(timeout: Option[FiniteDuration] = None,
                         refresh: Option[RefreshPolicy] = None,
                         requestsPerSecond: Option[Float] = None,
                         waitForActiveShards: Option[Int] = None,
                         waitForCompletion: Option[Boolean] = None
                        ) {

  import com.sksamuel.exts.OptionImplicits._

  def timeout(timeout: FiniteDuration): URLParameters =
    copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): URLParameters =
    copy(refresh = refresh.some)

  def requestsPerSecond(requestsPerSecond: Float): URLParameters =
    copy(requestsPerSecond = requestsPerSecond.some)

  def waitForActiveShards(waitForActiveShards: Int): URLParameters =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForCompletion(waitForCompletion: Boolean): URLParameters =
    copy(waitForCompletion = waitForCompletion.some)
}
