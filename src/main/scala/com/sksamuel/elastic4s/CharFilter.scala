package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

trait AnalyzerFilter {
  def name: String
}

trait AnalyzerFilterDefinition {
  def filterType: String
  def build(source: XContentBuilder): Unit
}

sealed trait CharFilter extends AnalyzerFilter

trait CharFilterDefinition extends CharFilter with AnalyzerFilterDefinition

case object HtmlStripCharFilter extends CharFilter {
  val name = "html_strip"
}

case class MappingCharFilter(name: String, mappings: (String, String)*)
  extends CharFilterDefinition {

  val filterType = "mapping"

  def build(source: XContentBuilder): Unit = {
    source.field("mappings", mappings.map({ case (k, v) => s"$k=>$v" }).toArray: _*)
  }
}

case class PatternReplaceCharFilter(name: String, pattern: String, replacement: String)
  extends CharFilterDefinition {

  val filterType = "pattern_replace"

  def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}

