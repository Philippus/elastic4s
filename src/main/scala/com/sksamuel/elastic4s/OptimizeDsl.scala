package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait OptimizeDsl {

  implicit def string2optimize(index: String) = optimize(index)
  def optimize(indexes: String*) = new OptimizeDefinition(indexes: _*)

  class OptimizeDefinition(indexes: String*) {

    val builder = Requests.optimizeRequest(indexes: _*)

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
}
