package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.delete.{DeleteIndexAction, DeleteIndexRequest}

/** @author Stephen Samuel */
trait DeleteIndexDsl {
  @deprecated
  implicit def string2delete(name: String) = new DeleteIndexDefinition(name)
  def deleteIndex(names: String*) = new DeleteIndexDefinition(names: _*)
}

class DeleteIndexDefinition(names: String*) extends IndicesRequestDefinition(DeleteIndexAction.INSTANCE) {
  private val builder = new DeleteIndexRequest().indices(names: _*)
  def build = builder
}