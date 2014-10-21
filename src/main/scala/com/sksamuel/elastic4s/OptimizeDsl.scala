package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait OptimizeDsl {

  case object optimize {
    def index(indexes: Iterable[String]): OptimizeDefinition = new OptimizeDefinition(indexes.toSeq: _*)
    def index(indexes: String*): OptimizeDefinition = index(indexes)
  }
  def optimize(indexes: String*) = new OptimizeDefinition(indexes: _*)

  class OptimizeDefinition(indexes: String*) {

    private val builder = Requests.optimizeRequest(indexes: _*)
    def build = builder

    def flush(flush: Boolean): OptimizeDefinition = {
      builder.flush(flush)
      this
    }
    def maxSegments(maxSegments: Int): OptimizeDefinition = {
      builder.maxNumSegments(maxSegments)
      this
    }
    def onlyExpungeDeletes(onlyExpungeDeletes: Boolean): OptimizeDefinition = {
      builder.onlyExpungeDeletes(onlyExpungeDeletes)
      this
    }
    def force(force: Boolean): OptimizeDefinition = {
      builder.force(force)
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
