package com.sksamuel.elastic4s.requests.indexes.admin.recovery

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class IndexRecoveryRequest(indices: Seq[String],
                                activeOnly: Option[Boolean] = None,
                                detailed: Option[Boolean] = None) {
  def activeOnly(boolean: Boolean): IndexRecoveryRequest = copy(activeOnly = boolean.some)
  def detailed(boolean: Boolean): IndexRecoveryRequest = copy(detailed = boolean.some)
}
