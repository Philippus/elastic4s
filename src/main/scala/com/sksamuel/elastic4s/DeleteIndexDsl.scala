package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest

/** @author Stephen Samuel */
trait DeleteIndexDsl {
  implicit def string2delete(name: String) = new DeleteIndexDefinition(name)
  def deleteIndex(names: String*) = new DeleteIndexDefinition(names: _*)
  class DeleteIndexDefinition(names: String*) {
    val builder = new DeleteIndexRequest().indices(names: _*)
  }
}
