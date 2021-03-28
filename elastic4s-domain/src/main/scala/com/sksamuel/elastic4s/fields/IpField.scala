package com.sksamuel.elastic4s.fields

case class IpField(name: String,
                   boost: Option[Double] = None,
                   copyTo: Seq[String] = Nil,
                   docValues: Option[Boolean] = None,
                   index: Option[Boolean] = None,
                   properties: Seq[ElasticField] = Nil,
                   store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "ip"
}
