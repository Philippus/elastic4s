package com.sksamuel.elastic4s.indexes.admin

import scala.language.implicitConversions

trait ForceMergeApi {

  def forceMerge(first: String, rest: String*): ForceMergeRequest = forceMerge(first +: rest)
  def forceMerge(indexes: Iterable[String]): ForceMergeRequest    = ForceMergeRequest(indexes.toSeq)
}
