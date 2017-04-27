package com.sksamuel.elastic4s.searches.collapse

trait CollapseApi {
  def collapseField(field: String): CollapseDefinition = CollapseDefinition(field)
}
