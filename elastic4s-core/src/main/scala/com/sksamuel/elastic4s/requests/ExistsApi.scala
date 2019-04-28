package com.sksamuel.elastic4s.requests

import com.sksamuel.elastic4s.Index

trait ExistsApi {
  def exists(id: String, index: Index): ExistsRequest = ExistsRequest(id, index)
}

case class ExistsRequest(id: String, index: Index)
