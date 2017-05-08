package com.sksamuel.elastic4s.indexes.admin

import com.sksamuel.exts.OptionImplicits._

case class ForceMergeDefinition(indexes: Seq[String],
                                flush: Option[Boolean] = None,
                                maxSegments: Option[Int] = None,
                                onlyExpungeDeletes: Option[Boolean] = None) {

  def flush(flush: Boolean): ForceMergeDefinition = copy(flush = flush.some)
  def maxSegments(maxSegments: Int): ForceMergeDefinition = copy(maxSegments = maxSegments.some)

  /**
    * Should the optimization only expunge deletes from the index, without full optimization.
    * Defaults to full optimization (<tt>false</tt>).
    */
  def onlyExpungeDeletes(expunge: Boolean): ForceMergeDefinition = copy(onlyExpungeDeletes = expunge.some)
}
