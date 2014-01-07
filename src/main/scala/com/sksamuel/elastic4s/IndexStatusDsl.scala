package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.status.{IndicesStatusAction, IndicesStatusRequest}

trait IndexStatusDsl {
  def status(indices: String*) = new IndexStatusDefinition(indices:_*)

  class IndexStatusDefinition(indices: String*) extends IndicesRequestDefinition(IndicesStatusAction.INSTANCE) {
    private def builder = new IndicesStatusRequest(indices:_*)
    def build = builder
  }
}
