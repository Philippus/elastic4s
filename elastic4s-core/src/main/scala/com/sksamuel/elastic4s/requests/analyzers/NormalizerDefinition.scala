package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

// Base class for normalizers that have custom parameters set.
abstract class NormalizerDefinition(val name: String) {

  def buildWithName(source: XContentBuilder): Unit = {
    source.startObject(name)
    build(source)
    source.endObject()
  }

  def buildWithName(): XContentBuilder = {
    val xc = XContentFactory.jsonBuilder()
    buildWithName(xc)
    xc.endObject()
  }

  def build(): XContentBuilder = {
    val xc = XContentFactory.jsonBuilder()
    build(xc)
    xc.endObject()
  }

  def build(source: XContentBuilder): Unit

  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    build(builder)
    builder.endObject()
  }
}

case class CustomNormalizerDefinition(override val name: String, filters: Seq[AnalyzerFilter] = Nil)
    extends NormalizerDefinition(name) {

  def build(source: XContentBuilder): Unit = {
    source.field("type", "custom")
    val tokenFilters = filters.collect { case token: TokenFilter => token }
    val charFilters  = filters.collect { case char: CharFilter   => char }
    if (tokenFilters.nonEmpty)
      source.array("filter", tokenFilters.map(_.name).toArray)
    if (charFilters.nonEmpty)
      source.array("char_filter", charFilters.map(_.name).toArray)
  }

  def filters(filters: Seq[AnalyzerFilter]): CustomNormalizerDefinition = copy(filters = filters)
  def addFilter(filter: AnalyzerFilter): CustomNormalizerDefinition     = copy(filters = filters :+ filter)
}

object CustomNormalizerDefinition {
  def apply(name: String, first: AnalyzerFilter, rest: AnalyzerFilter*): CustomNormalizerDefinition =
    CustomNormalizerDefinition(name, first +: rest)
}
