package com.sksamuel.elastic4s.fields

object RankFeatureField {
  val `type`: String = "rank_feature"
}
case class RankFeatureField(name: String, positiveScoreImpact: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = RankFeatureField.`type`
}
