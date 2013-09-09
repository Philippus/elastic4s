package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.admin.indices.optimize.OptimizeAction

/** @author Stephen Samuel */
trait OptimizeDsl {

  def optimize(indexes: String*) = new OptimizeDefinition(indexes: _*)

  class OptimizeDefinition(indexes: String*) extends IndicesRequestDefinition(OptimizeAction.INSTANCE) {

    private val builder = Requests.optimizeRequest(indexes: _*)
    def build = builder

    def maxSegments(maxSegments: Int): OptimizeDefinition = {
      builder.maxNumSegments(maxSegments)
      this
    }
    def flush(flush: Boolean): OptimizeDefinition = {
      builder.flush(flush)
      this
    }
    def onlyExpungeDeletes(onlyExpungeDeletes: Boolean): OptimizeDefinition = {
      builder.onlyExpungeDeletes(onlyExpungeDeletes)
      this
    }
    def refresh(refresh: Boolean): OptimizeDefinition = {
      builder.refresh(refresh)
      this
    }
    def waitForMerge(waitForMerge: Boolean): OptimizeDefinition = {
      builder.waitForMerge()
      this
    }

  }

  object OptimizeDefinition {
    implicit def apply(index: String): OptimizeDefinition = optimize(index)
  }

}
