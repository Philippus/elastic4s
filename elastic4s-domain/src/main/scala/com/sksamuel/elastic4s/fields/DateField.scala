package com.sksamuel.elastic4s.fields

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class DateField(name: String,
                     boost: Option[Double] = None,
                     copyTo: Seq[String] = Nil,
                     docValues: Option[Boolean] = None,
                     format: Option[String] = None,
                     locale: Option[String] = None,
                     ignoreMalformed: Option[Boolean] = None,
                     index: Option[Boolean] = None,
                     nullValue: Option[String] = None,
                     store: Option[Boolean] = None,
                     meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = "date"
  def format(format: String): DateField = copy(format = format.some)
  def nullValue(nullValue: String): DateField = copy(nullValue = nullValue.some)
}
