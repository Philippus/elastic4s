package com.sksamuel.elastic4s.fields

case class RankFeatureField(name: String,
                            positiveScoreImpact: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "rank_feature"
}
