package com.sksamuel.elastic4s.bulk

import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.Duration

case class BulkDefinition(requests: Seq[BulkCompatibleDefinition],
                          timeout: Option[String] = None,
                          refresh: Option[String] = None) {

  def timeout(timeout: Duration): BulkDefinition = copy(timeout = (timeout.toNanos + "n").some)
  def timeout(timeout: String): BulkDefinition = copy(timeout = timeout.some)
  def refresh(refresh: String): BulkDefinition = copy(refresh = refresh.some)
}
