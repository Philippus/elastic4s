package com.sksamuel.elastic4s

/** @author Stephen Samuel */
trait CharFilter
object CharFilter {
  case object HtmlStripCharFilter extends CharFilter
  case class MappingCharFilter(map: Map[String, String]) extends CharFilter
  case class PatternReplaceCharFilter(pattern: String, replacement: String) extends CharFilter
}
