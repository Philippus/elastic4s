package com.sksamuel.elastic4s.fields

object FlattenedField {
  val `type`: String = "flattened"
}
case class FlattenedField(
    name: String,
    boost: Option[Double] = None,
    docValues: Option[Boolean] = None,
    depthLimit: Option[Int] = None,
    eagerGlobalOrdinals: Option[Boolean] = None,
    ignoreAbove: Option[Int] = None,
    index: Option[Boolean] = None,
    indexOptions: Option[String] = None,
    nullValue: Option[String] = None,
    similarity: Option[String] = None,
    splitQueriesOnWhitespace: Option[Boolean] = None,
    meta: Map[String, String] = Map.empty
) extends ElasticField {
  override def `type`: String = FlattenedField.`type`
}
