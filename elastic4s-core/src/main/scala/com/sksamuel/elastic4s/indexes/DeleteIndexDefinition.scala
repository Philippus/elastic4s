package com.sksamuel.elastic4s.indexes

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest

case class DeleteIndexDefinition(indexes: Seq[String]) {
  private val builder = new DeleteIndexRequest().indices(indexes: _*)
  def build = builder
}
