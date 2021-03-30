package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.collapse.CollapseRequest

trait CollapseApi {
  def collapseField(field: String): CollapseRequest = CollapseRequest(field)
}
