package com.sksamuel.elastic4s.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}
@deprecated("use DocumentSource", "1.0")
trait Source extends DocumentSource

trait DocumentMap {
  def map: Map[String, Any]
}