package com.sksamuel.elastic4s.fields

object TokenCountField {
  val `type`: String = "token_count"
}
case class TokenCountField(
    name: String,
    analyzer: Option[String] = None,
    boost: Option[Double] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    enablePositionIncrements: Option[Boolean] = None,
    index: Option[Boolean] = None,
    nullValue: Option[String] = None,
    store: Option[Boolean] = None,
    meta: Map[String, Any] = Map.empty
) extends ElasticField {
  override def `type`: String = TokenCountField.`type`
}
