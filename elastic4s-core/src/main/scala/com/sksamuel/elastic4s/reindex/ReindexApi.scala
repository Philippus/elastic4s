package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.exts.OptionImplicits._

trait ReindexApi {

  def reindex(sourceIndexes: Indexes): ReindexExpectsTarget = new ReindexExpectsTarget(sourceIndexes)
  class ReindexExpectsTarget(sourceIndexes: Indexes) {
    def into(index: String): ReindexDefinition = ReindexDefinition(sourceIndexes, index)
    def into(index: String, `type`: String): ReindexDefinition = ReindexDefinition(sourceIndexes, index, `type`.some)
  }
}
