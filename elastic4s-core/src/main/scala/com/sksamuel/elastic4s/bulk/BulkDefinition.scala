package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.Duration

case class BulkDefinition(requests: Seq[BulkCompatibleDefinition],
                          timeout: Option[String] = None,
                          refresh: Option[RefreshPolicy] = None) {

  def timeout(timeout: Duration): BulkDefinition = copy(timeout = (timeout.toNanos + "n").some)
  def timeout(timeout: String): BulkDefinition = copy(timeout = timeout.some)

  @deprecated("use the typed version, refresh(RefreshPolicy)", "6.0.0")
  def refresh(refresh: String): BulkDefinition = copy(refresh = RefreshPolicy.valueOf(refresh).some)
  def refresh(refresh: RefreshPolicy): BulkDefinition = copy(refresh = refresh.some)
}

trait BulkCompatibleDefinition
