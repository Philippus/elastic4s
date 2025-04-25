package com.sksamuel.elastic4s.fields

object RankVectorsField  {
  val `type`: String = "rank_vectors"
}
case class RankVectorsField(name: String, elementType: Option[String] = None, dims: Option[Int] = None)
    extends ElasticField {
  override def `type`: String = RankVectorsField.`type`
}
