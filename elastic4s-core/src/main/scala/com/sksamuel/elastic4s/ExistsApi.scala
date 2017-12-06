package com.sksamuel.elastic4s

trait ExistsApi {
  def exists(id: String, index: Index, `type`: String): ExistsDefinition = ExistsDefinition(id, index, `type`)
}

case class ExistsDefinition(id: String, index: Index, `type`: String)
