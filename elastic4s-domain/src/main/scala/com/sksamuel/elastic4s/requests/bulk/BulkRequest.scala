package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.common.RefreshPolicy

import scala.concurrent.duration.Duration

case class BulkRequest(requests: Seq[BulkCompatibleRequest],
                       timeout: Option[String] = None,
                       refresh: Option[RefreshPolicy] = None) {

  def timeout(timeout: Duration): BulkRequest = copy(timeout = (timeout.toNanos + "nanos").some)
  def timeout(timeout: String): BulkRequest = copy(timeout = timeout.some)

  def refresh(refresh: RefreshPolicy): BulkRequest = copy(refresh = refresh.some)

  def refreshImmediately: BulkRequest = refresh(RefreshPolicy.IMMEDIATE)
  def waitForRefresh: BulkRequest = refresh(RefreshPolicy.WaitFor)
}
