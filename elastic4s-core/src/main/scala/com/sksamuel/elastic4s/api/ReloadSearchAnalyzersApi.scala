package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.reloadsearchanalyzers.ReloadSearchAnalyzersRequest

trait ReloadSearchAnalyzersApi {
  def reloadSearchAnalyzers(index: String): ReloadSearchAnalyzersRequest = ReloadSearchAnalyzersRequest(Indexes(index))
  def reloadSearchAnalyzers(indexes: Indexes): ReloadSearchAnalyzersRequest = ReloadSearchAnalyzersRequest(indexes)
  def reloadSearchAnalyzers(indexes: Seq[String]): ReloadSearchAnalyzersRequest = ReloadSearchAnalyzersRequest(Indexes(indexes))
}
