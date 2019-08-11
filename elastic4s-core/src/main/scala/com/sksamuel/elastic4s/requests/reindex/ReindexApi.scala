package com.sksamuel.elastic4s.requests.reindex

import com.sksamuel.elastic4s.{Index, Indexes}

trait ReindexApi {
  def reindex(source: String, target: Index): ReindexRequest  = reindex(Indexes(source), target)
  def reindex(source: Indexes, target: Index): ReindexRequest = ReindexRequest(source, target)
  def reindex(source: Index, target: Index): ReindexRequest   = ReindexRequest(source.toIndexes, target)
}
