package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

@deprecated("use new analysis package", "7.7.0")
trait AnalyzerFilter {
  def name: String
}

@deprecated("use new analysis package", "7.7.0")
case class PredefinedTokenFilter(name: String) extends TokenFilter

@deprecated("use new analysis package", "7.7.0")
case class PredefinedCharFilter(name: String)  extends CharFilter

@deprecated("use new analysis package", "7.7.0")
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

@deprecated("use new analysis package", "7.7.0")
trait CharFilter extends AnalyzerFilter

@deprecated("use new analysis package", "7.7.0")
trait CharFilterDefinition extends CharFilter with AnalyzerFilterDefinition

@deprecated("use new analysis package", "7.7.0")
case object HtmlStripCharFilter extends CharFilter {
  val name = "html_strip"
}

@deprecated("use new analysis package", "7.7.0")
case class MappingCharFilter(name: String, mappings: (String, String)*) extends CharFilterDefinition {

  val filterType = "mapping"

  def build(source: XContentBuilder): Unit =
    source.array("mappings", mappings.map({ case (k, v) => s"$k=>$v" }).toArray)
}

@deprecated("use new analysis package", "7.7.0")
case class PatternReplaceCharFilter(name: String, pattern: String, replacement: String) extends CharFilterDefinition {

  val filterType = "pattern_replace"

  def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}
