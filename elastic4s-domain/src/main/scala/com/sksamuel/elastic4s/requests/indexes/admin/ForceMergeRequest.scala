package com.sksamuel.elastic4s.requests.indexes.admin

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class ForceMergeRequest(indexes: Seq[String],
                             flush: Option[Boolean] = None,
                             maxSegments: Option[Int] = None,
                             onlyExpungeDeletes: Option[Boolean] = None) {

  def flush(flush: Boolean): ForceMergeRequest = copy(flush = flush.some)
  def maxSegments(maxSegments: Int): ForceMergeRequest = copy(maxSegments = maxSegments.some)

  /**
    * Should the optimization only expunge deletes from the index, without full optimization.
    * Defaults to full optimization (<tt>false</tt>).
    */
  def onlyExpungeDeletes(expunge: Boolean): ForceMergeRequest = copy(onlyExpungeDeletes = expunge.some)
}
