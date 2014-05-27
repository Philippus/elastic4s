package com.sksamuel.elastic4s.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}

trait DocumentMap {
  def map: Map[String, Any]
}