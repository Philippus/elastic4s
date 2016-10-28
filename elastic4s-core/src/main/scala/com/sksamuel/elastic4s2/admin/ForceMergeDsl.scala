package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.Executable
import org.elasticsearch.action.admin.indices.forcemerge.{ForceMergeRequest, ForceMergeResponse}
import org.elasticsearch.client.{Client, Requests}

import scala.concurrent.Future
import scala.language.implicitConversions

trait ForceMergeDsl {

  def forceMerge(first: String, rest: String*): ForceMergeDefinition = forceMerge(first +: rest)
  def forceMerge(indexes: Iterable[String]): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)

  implicit object ForceMergeDefinitionExecutable
    extends Executable[ForceMergeDefinition, ForceMergeResponse, ForceMergeResponse] {
    override def apply(c: Client, t: ForceMergeDefinition): Future[ForceMergeResponse] = {
      injectFuture(c.admin.indices.forceMerge(t.build, _))
    }
  }
}

case class ForceMergeDefinition(indexes: Seq[String]) {

  private val builder = Requests.forceMergeRequest(indexes: _*)
  def build: ForceMergeRequest = builder

  def flush(flush: Boolean): ForceMergeDefinition = {
    builder.flush(flush)
    this
  }

  def maxSegments(maxSegments: Int): ForceMergeDefinition = {
    builder.maxNumSegments(maxSegments)
    this
  }

  /**
   * Should the optimization only expunge deletes from the index, without full optimization.
   * Defaults to full optimization (<tt>false</tt>).
   */

  def onlyExpungeDeletes(onlyExpungeDeletes: Boolean): ForceMergeDefinition = {
    builder.onlyExpungeDeletes(onlyExpungeDeletes)
    this
  }
}
