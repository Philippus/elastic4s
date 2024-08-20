package com.sksamuel.elastic4s.fields

object RankFeaturesField {
  val `type`: String = "rank_features"
}
case class RankFeaturesField(name: String,
                             positiveScoreImpact: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = RankFeaturesField.`type`
}
