package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits._

object IntegerField {
  val `type`: String = "integer"
}
case class IntegerField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    index: Option[Boolean] = None,
    nullValue: Option[Int] = None,
    store: Option[Boolean] = None,
    meta: Map[String, String] = Map.empty,
    timeSeriesDimension: Option[Boolean] = None,
    timeSeriesMetric: Option[String] = None,
    fields: List[ElasticField] = Nil
) extends NumberField[Int] {
  override def `type`: String = IntegerField.`type`

  def coerce(coerce: Boolean): IntegerField = copy(coerce = coerce.some)

  def stored(store: Boolean): IntegerField = copy(store = store.some)

  def nullValue(nullValue: Int): IntegerField = copy(nullValue = nullValue.some)
}
