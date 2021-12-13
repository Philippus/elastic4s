package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits._

object DateField {
  val `type`: String = "date"
}
// https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html
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
  override def `type`: String = DateField.`type`

  def format(format: String): DateField = copy(format = format.some)

  def boost(boost: Double): DateField = copy(boost = boost.some)

  def docValues(docValues: Boolean): DateField = copy(docValues = docValues.some)

  def locale(locale: String): DateField = copy(locale = locale.some)

  def ignoreMalformed(ignoreMalformed: Boolean): DateField = copy(ignoreMalformed = ignoreMalformed.some)

  def index(index: Boolean): DateField = copy(index = index.some)

  def nullValue(nullValue: String): DateField = copy(nullValue = nullValue.some)

  def store(store: Boolean): DateField = copy(store = store.some)
}
