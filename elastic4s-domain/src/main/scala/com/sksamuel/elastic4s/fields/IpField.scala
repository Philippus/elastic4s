package com.sksamuel.elastic4s.fields

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class IpField(name: String,
                   boost: Option[Double] = None,
                   copyTo: Seq[String] = Nil,
                   docValues: Option[Boolean] = None,
                   index: Option[Boolean] = None,
                   properties: Seq[ElasticField] = Nil,
                   store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "ip"
  def boost(boost: Double): IpField = copy(boost = boost.some)
}
