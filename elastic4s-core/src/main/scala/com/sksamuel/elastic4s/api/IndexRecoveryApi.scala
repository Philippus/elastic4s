package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexes.admin.recovery.IndexRecoveryRequest

trait IndexRecoveryApi {
  def recoverIndex(first: String, rest: String*): IndexRecoveryRequest = recoverIndex(first +: rest)
  def recoverIndex(indexes: Iterable[String]): IndexRecoveryRequest    = IndexRecoveryRequest(indexes.toSeq)
}

