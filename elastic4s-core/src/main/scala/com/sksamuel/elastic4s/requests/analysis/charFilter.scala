package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

trait CharFilter {
  def name: String
  def build: XContentBuilder
}

case class MappingCharFilter(override val name: String,
                             mappings: Map[String, String]) extends CharFilter {
  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "mapping")
    b.startArray("mappings")
    mappings.foreach { case (k, v) =>
      b.value(s"$k=>$v")
    }
    b.endObject()
    b.endObject()
  }
}

case class PatternReplaceCharFilter(override val name: String,
                                    pattern: String,
                                    replacement: String) extends CharFilter {
  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "pattern_replace")
    b.field("pattern", pattern)
    b.field("replacement", replacement)
    b.endObject()
  }
}
