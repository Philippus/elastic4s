package com.sksamuel.elastic4s.requests.reindex

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

trait ReindexApi {

  def reindex(source: String, target: Index): ReindexRequest  = reindex(Indexes(source), target)
  def reindex(source: Indexes, target: Index): ReindexRequest = ReindexRequest(source, target)
  def reindex(source: Index, target: Index): ReindexRequest   = ReindexRequest(source.toIndexes, target)

  @deprecated("use reindex(from, to)", "6.0.0")
  def reindex(sourceIndexes: Indexes): ReindexExpectsTarget = new ReindexExpectsTarget(sourceIndexes)
  class ReindexExpectsTarget(sourceIndexes: Indexes) {
    def into(index: String): ReindexRequest                 = ReindexRequest(sourceIndexes, index)
    def into(index: String, `type`: String): ReindexRequest = ReindexRequest(sourceIndexes, index, `type`.some)
  }
}
