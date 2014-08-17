package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait OptimizeDsl {

  def optimize = new OptimizeExpectIndex
  def optimize(indexes: String*) = new OptimizeDefinition(indexes: _*)

  class OptimizeExpectIndex {
    def index(indexes: Iterable[String]): OptimizeDefinition = new OptimizeDefinition(indexes.toSeq: _*)
    def index(indexes: String*): OptimizeDefinition = index(indexes)
  }

  class OptimizeDefinition(indexes: String*) {

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
    def waitForMerge(waitForMerge: Boolean): OptimizeDefinition = {
      if (waitForMerge)
        builder.waitForMerge()
      this
    }
  }

  object OptimizeDefinition {
    implicit def apply(index: String): OptimizeDefinition = optimize(index)
  }

}
