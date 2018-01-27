package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

trait ReindexApi {

  def reindex(source: String, target: Index): ReindexDefinition  = reindex(Indexes(source), target)
  def reindex(source: Indexes, target: Index): ReindexDefinition = ReindexDefinition(source, target)
  def reindex(source: Index, target: Index): ReindexDefinition   = ReindexDefinition(source.toIndexes, target)

  @deprecated("use reindex(from, to)", "6.0.0")
  def reindex(sourceIndexes: Indexes): ReindexExpectsTarget = new ReindexExpectsTarget(sourceIndexes)
  class ReindexExpectsTarget(sourceIndexes: Indexes) {
    def into(index: String): ReindexDefinition                 = ReindexDefinition(sourceIndexes, index)
    def into(index: String, `type`: String): ReindexDefinition = ReindexDefinition(sourceIndexes, index, `type`.some)
  }
}
