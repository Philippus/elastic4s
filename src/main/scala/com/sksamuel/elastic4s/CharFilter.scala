package com.sksamuel.elastic4s

/** @author Stephen Samuel */
trait CharacterFilter
object CharacterFilter {
  case object HtmlStripCharFilter extends CharacterFilter
  case class MappingCharFilter(map: Map[String, String]) extends CharacterFilter
  case class PatternReplaceCharFilter(pattern: String, replacement: String) extends CharacterFilter
}
