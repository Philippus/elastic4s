package com.sksamuel.elastic4s.indexes.admin

import scala.language.implicitConversions

trait ForceMergeApi {

  def forceMerge(first: String, rest: String*): ForceMergeDefinition = forceMerge(first +: rest)
  def forceMerge(indexes: Iterable[String]): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)
}
