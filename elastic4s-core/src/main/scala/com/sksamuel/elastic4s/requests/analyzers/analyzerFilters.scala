package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

trait AnalyzerFilter {
  def name: String
}

case class PredefinedTokenFilter(name: String) extends TokenFilter
case class PredefinedCharFilter(name: String)  extends CharFilter

trait AnalyzerFilterDefinition {
  def filterType: String
  protected[elastic4s] def build(source: XContentBuilder): Unit
  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    builder.field("type", filterType)
    build(builder)
    builder.endObject()
    builder
  }
}

trait CharFilter extends AnalyzerFilter

trait CharFilterDefinition extends CharFilter with AnalyzerFilterDefinition

case object HtmlStripCharFilter extends CharFilter {
  val name = "html_strip"
}

case class MappingCharFilter(name: String, mappings: (String, String)*) extends CharFilterDefinition {

  val filterType = "mapping"

  def build(source: XContentBuilder): Unit =
    source.array("mappings", mappings.map({ case (k, v) => s"$k=>$v" }).toArray)
}

case class PatternReplaceCharFilter(name: String, pattern: String, replacement: String) extends CharFilterDefinition {

  val filterType = "pattern_replace"

  def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}
