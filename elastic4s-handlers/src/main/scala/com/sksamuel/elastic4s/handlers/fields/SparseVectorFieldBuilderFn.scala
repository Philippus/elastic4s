package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.PruningConfig

object SparseVectorFieldBuilderFn {
  private def getPruningConfig(values: Map[String, Any]): PruningConfig =
    PruningConfig(
      values.get("tokens_freq_ratio_threshold").map(_.asInstanceOf[Int]),
      values.get("tokens_weight_threshold").map(_.asInstanceOf[Double].toFloat)
    )

  private def getIndexOptions(values: Map[String, Any]): SparseVectorIndexOptions = {
    SparseVectorIndexOptions(
      values("prune").asInstanceOf[Boolean],
      pruningConfig = values.get("pruning_config").map(_.asInstanceOf[Map[String, Any]]).map(getPruningConfig)
    )
  }

  def toField(name: String, values: Map[String, Any]): SparseVectorField = SparseVectorField(
    name,
    values("store").asInstanceOf[Boolean],
    indexOptions = values.get("index_options").map(_.asInstanceOf[Map[String, Any]]).map(getIndexOptions)
  )

  def build(field: SparseVectorField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("store", field.store)
    field.indexOptions.foreach { indexOptions =>
      builder.startObject("index_options")
      builder.field("prune", indexOptions.prune)
      indexOptions.pruningConfig.foreach { pruningConfig =>
        if (pruningConfig.tokensFreqRatioThreshold.nonEmpty || pruningConfig.tokensWeighThreshold.nonEmpty) {
          builder.startObject("pruning_config")
          pruningConfig.tokensFreqRatioThreshold.foreach(builder.field("tokens_freq_ratio_threshold", _))
          pruningConfig.tokensWeighThreshold.foreach(builder.field("tokens_weight_threshold", _))
          builder.endObject()
        }
      }
      builder.endObject()
    }
    builder.endObject()
  }
}
