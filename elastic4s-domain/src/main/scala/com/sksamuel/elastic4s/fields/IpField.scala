package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class IpField(name: String,
                   boost: Option[Double] = None,
                   docValues: Option[Boolean] = None,
                   ignoreMalformed: Option[Boolean] = None,
                   index: Option[Boolean] = None,
                   nullValue: Option[String] = None,
                   store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "ip"
  def boost(boost: Double): IpField = copy(boost = boost.some)
}
