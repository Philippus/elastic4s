package com.sksamuel.elastic4s.source

import com.fasterxml.jackson.databind.JsonNode

/** @author Stephen Samuel */
class JacksonSource(root: JsonNode) extends DocumentSource {
  def json = root.toString
}
object JacksonSource {
  def apply(root: JsonNode) = new JacksonSource(root)
}
