package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOptionImplicits

case class DoubleField(name: String,
                       boost: Option[Double] = None,
                       coerce: Option[Boolean] = None,
                       copyTo: Seq[String] = Nil,
                       docValues: Option[Boolean] = None,
                       ignoreMalformed: Option[Boolean] = None,
                       index: Option[Boolean] = None,
                       nullValue: Option[Double] = None,
                       store: Option[Boolean] = None,
                       meta: Map[String, Any] = Map.empty) extends NumberField[Double] {
  override def `type`: String = "double"
  def coerce(coerce: Boolean): DoubleField = copy(coerce = coerce.some)
  def stored(store: Boolean): DoubleField = copy(store = store.some)
  def ignoreMalformed(ignoreMalformed: Boolean): DoubleField = copy(ignoreMalformed = ignoreMalformed.some)
  def boost(boost: Double): DoubleField = copy(boost = boost.some)
}
