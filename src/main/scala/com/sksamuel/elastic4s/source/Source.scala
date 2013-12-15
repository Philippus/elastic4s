package com.sksamuel.elastic4s.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}
@deprecated
trait Source extends DocumentSource