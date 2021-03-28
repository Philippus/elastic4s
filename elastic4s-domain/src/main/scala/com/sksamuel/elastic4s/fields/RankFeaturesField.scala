package com.sksamuel.elastic4s.fields

case class RankFeaturesField(name: String) extends ElasticField {
  override def `type`: String = "rank_features"
}
