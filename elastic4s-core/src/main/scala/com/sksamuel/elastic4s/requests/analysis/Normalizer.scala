package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

case class CharFilter(name: String)
case class TokenFilter(name: String)

case class Normalizer(name: String,
                      charFilters: List[CharFilter],
                      tokenFilters: List[TokenFilter])

trait Builder[T] {
  def build(t: T): XContentBuilder
}

object NormalizerBuilder extends Builder[Normalizer] {
  override def build(t: Normalizer): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("normalizer")
    builder.startObject(t.name)
    builder.field("type", "custom")
    if (t.tokenFilters.nonEmpty)
      builder.array("filter", t.tokenFilters.map(_.name).toArray)
    if (t.charFilters.nonEmpty)
      builder.array("char_filter", t.charFilters.map(_.name).toArray)
    builder.endObject()
  }
}
