package com.sksamuel.elastic4s.indexes.admin

import com.sksamuel.exts.OptionImplicits._

trait IndexRecoveryApi {
  def recoverIndex(first: String, rest: String*): IndexRecoveryDefinition = recoverIndex(first +: rest)
  def recoverIndex(indexes: Iterable[String]): IndexRecoveryDefinition = IndexRecoveryDefinition(indexes.toSeq)
}

case class IndexRecoveryDefinition(indices: Seq[String],
                                   activeOnly: Option[Boolean] = None,
                                   detailed: Option[Boolean] = None) {
  def activeOnly(boolean: Boolean): IndexRecoveryDefinition = copy(activeOnly = boolean.some)
  def detailed(boolean: Boolean): IndexRecoveryDefinition = copy(detailed = boolean.some)
}
