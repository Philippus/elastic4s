package com.sksamuel.elastic4s.requests.indexes.admin

import com.sksamuel.exts.OptionImplicits._

trait IndexRecoveryApi {
  def recoverIndex(first: String, rest: String*): IndexRecoveryRequest = recoverIndex(first +: rest)
  def recoverIndex(indexes: Iterable[String]): IndexRecoveryRequest    = IndexRecoveryRequest(indexes.toSeq)
}

case class IndexRecoveryRequest(indices: Seq[String],
                                activeOnly: Option[Boolean] = None,
                                detailed: Option[Boolean] = None) {
  def activeOnly(boolean: Boolean): IndexRecoveryRequest = copy(activeOnly = boolean.some)
  def detailed(boolean: Boolean): IndexRecoveryRequest   = copy(detailed = boolean.some)
}
