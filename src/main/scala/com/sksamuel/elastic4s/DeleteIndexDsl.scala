package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest

/** @author Stephen Samuel */
trait DeleteIndexDsl {
  def deleteIndex(names: String*) = new DeleteIndexDefinition(names: _*)
}

class DeleteIndexDefinition(names: String*) {
  private val builder = new DeleteIndexRequest().indices(names: _*)
  def build = builder
}