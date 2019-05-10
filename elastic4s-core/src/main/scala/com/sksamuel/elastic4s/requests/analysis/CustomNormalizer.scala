package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

trait Normalizer {
  def name: String
  def build: XContentBuilder
}

case class CustomNormalizer(override val name: String,
                            charFilters: List[String],
                            tokenFilters: List[String]) extends Normalizer {
  override def build: XContentBuilder = NormalizerBuilder.build(this)
}

trait Builder[T] {
  def build(t: T): XContentBuilder
}

object NormalizerBuilder extends Builder[CustomNormalizer] {
  override def build(t: CustomNormalizer): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", "custom")
    if (t.tokenFilters.nonEmpty)
      builder.array("filter", t.tokenFilters)
    if (t.charFilters.nonEmpty)
      builder.array("char_filter", t.charFilters)
    builder.endObject()
  }
}
