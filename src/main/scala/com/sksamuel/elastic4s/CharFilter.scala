package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
abstract class CharacterFilterDefinition(val name: String, val filterType: String) {
  def build(source: XContentBuilder): Unit
}

// name doesn't really exist - the name is "html"? actually, no, name exists - html can be used directly

case class MappingCharFilterDefinition(override val name: String, mappings: Map[String, String]) extends CharacterFilterDefinition(name, "mapping") {
  override def build(source: XContentBuilder): Unit = {
    source.field("mappings", mappings.map({ case (k, v) => s"$k=>$v" }) toArray: _*)
  }
}
case class PatternReplaceCharFilterDefinition(override val name: String, pattern: String, replacement: String) extends CharacterFilterDefinition(name, "pattern_replace") {
  override def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}