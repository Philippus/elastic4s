package com.sksamuel.elastic4s.requests.common

case class FetchSourceContext(fetchSource: Boolean,
                              includes: Set[String] = Set.empty,
                              excludes: Set[String] = Set.empty)

object FetchSourceContext {
  def apply(fetchSource: Boolean,
            includes: Array[String],
            excludes: Array[String]): FetchSourceContext =
    FetchSourceContext(fetchSource, includes.toSet, excludes.toSet)

  def apply(fetchSource: Boolean, includes: Array[String]): FetchSourceContext =
    FetchSourceContext(fetchSource, includes.toSet)
}
