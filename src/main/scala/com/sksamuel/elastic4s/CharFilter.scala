package com.sksamuel.elastic4s

/** @author Stephen Samuel */
trait CharFilter
object CharFilter {
  case object HtmlStripCharFilter
  case class MappingCharFilter(map: Map[String, String])
  case class PatternReplaceCharFilter(pattern: String, replacement: String)
}
