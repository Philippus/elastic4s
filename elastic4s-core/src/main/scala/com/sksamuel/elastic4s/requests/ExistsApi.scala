package com.sksamuel.elastic4s.requests

import com.sksamuel.elastic4s.Index

trait ExistsApi {
  def exists(id: String, index: Index, `type`: String): ExistsRequest = ExistsRequest(id, index, `type`)
}

case class ExistsRequest(id: String, index: Index, `type`: String)
