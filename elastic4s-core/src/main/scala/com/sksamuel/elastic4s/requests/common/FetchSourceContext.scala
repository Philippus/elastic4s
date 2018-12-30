package com.sksamuel.elastic4s.requests.common

case class FetchSourceContext(fetchSource: Boolean,
                              includes: Array[String] = Array.empty,
                              excludes: Array[String] = Array.empty)
