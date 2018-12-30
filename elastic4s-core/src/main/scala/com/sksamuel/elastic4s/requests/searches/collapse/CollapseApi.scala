package com.sksamuel.elastic4s.requests.searches.collapse

trait CollapseApi {
  def collapseField(field: String): CollapseRequest = CollapseRequest(field)
}
