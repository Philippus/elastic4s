package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.Duration

case class BulkRequest(requests: Seq[BulkCompatibleRequest],
                       timeout: Option[String] = None,
                       refresh: Option[RefreshPolicy] = None) {

  def timeout(timeout: Duration): BulkRequest = copy(timeout = (timeout.toNanos + "n").some)
  def timeout(timeout: String): BulkRequest   = copy(timeout = timeout.some)

  @deprecated("use the typed version, refresh(RefreshPolicy)", "6.0.0")
  def refresh(refresh: String): BulkRequest        = copy(refresh = RefreshPolicy.valueOf(refresh).some)
  def refresh(refresh: RefreshPolicy): BulkRequest = copy(refresh = refresh.some)

  @deprecated("use refreshImmediately", "6.0.0")
  def immediateRefresh(): BulkRequest = refresh(RefreshPolicy.Immediate)

  def refreshImmediately: BulkRequest = refresh(RefreshPolicy.IMMEDIATE)
  def waitForRefresh(): BulkRequest   = refresh(RefreshPolicy.WaitFor)
}

trait BulkCompatibleRequest
