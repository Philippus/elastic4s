package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse
import org.elasticsearch.client.{ Client, Requests }

import scala.concurrent.Future

/** @author Stephen Samuel */
trait OptimizeDsl {

  def optimize(indexes: String*) = new OptimizeDefinition(indexes: _*)

  object OptimizeDefinition {
    implicit def apply(index: String): OptimizeDefinition = optimize(index)
  }

  implicit object OptimizeDefinitionExecutable
      extends Executable[OptimizeDefinition, OptimizeResponse] {
    override def apply(c: Client, t: OptimizeDefinition): Future[OptimizeResponse] = {
      injectFuture(c.admin.indices.optimize(t.build, _))
    }
  }
}

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
}
