package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexes.admin.ForceMergeRequest

trait ForceMergeApi {

  def forceMerge(first: String, rest: String*): ForceMergeRequest = forceMerge(first +: rest)
  def forceMerge(indexes: Iterable[String]): ForceMergeRequest    = ForceMergeRequest(indexes.toSeq)
}
