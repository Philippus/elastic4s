package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.source.DocumentSource

/** @author Stephen Samuel */
class JacksonSource(root: JsonNode) extends DocumentSource {
  def json = root.toString
}
object JacksonSource {
  def apply(root: JsonNode) = new JacksonSource(root)
}
