package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class PruningConfig(tokensFreqRatioThreshold: Option[Int] = None,
                         tokensWeighThreshold: Option[Float] = None,
                         onlyScorePrunedTokens: Option[Boolean] = None)

case class SparseVectorQuery(field: String,
                             inferenceId: Option[String] = None,
                             query: Option[String] = None,
                             queryVector: Map[String, Double] = Map.empty[String, Double],
                             boost: Option[Double] = None,
                             queryName: Option[String] = None,
                             prune: Option[Boolean] = None,
                             pruningConfig: Option[PruningConfig] = None)
  extends Query {

  def boost(boost: Double): SparseVectorQuery = copy(boost = boost.some)

  def queryName(queryName: String): SparseVectorQuery = copy(queryName = queryName.some)

  def prune(prune: Boolean): SparseVectorQuery = copy(prune = prune.some)

  def pruningConfig(pruningConfig: PruningConfig): SparseVectorQuery = copy(pruningConfig = pruningConfig.some)
}
