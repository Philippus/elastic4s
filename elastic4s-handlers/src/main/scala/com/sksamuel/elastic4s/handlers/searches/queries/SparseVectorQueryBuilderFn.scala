package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.SparseVectorQuery

object SparseVectorQueryBuilderFn {
  def apply(q: SparseVectorQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("sparse_vector")
    builder.field("field", q.field)

    q.inferenceId.foreach(builder.field("inference_id", _))
    q.query.foreach(builder.field("query", _))
    if (q.queryVector.nonEmpty) {
      builder.startObject("query_vector")
      q.queryVector.foreach { case (k, v) => builder.field(k, v) }
      builder.endObject()
    }
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    q.prune.foreach(builder.field("prune", _))
    q.pruningConfig.foreach { pc =>
      if (
        pc.tokensFreqRatioThreshold.nonEmpty || pc.tokensWeighThreshold.nonEmpty || pc.onlyScorePrunedTokens.nonEmpty
      ) {
        builder.startObject("pruning_config")
        pc.tokensFreqRatioThreshold.foreach(builder.field("tokens_freq_ratio_threshold", _))
        pc.tokensWeighThreshold.foreach(builder.field("tokens_weight_threshold", _))
        pc.onlyScorePrunedTokens.foreach(builder.field("only_score_pruned_tokens", _))
        builder.endObject()
      }
    }
    builder.endObject()
    builder
  }
}
