package com.sksamuel.elastic4s

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.Show
import cats.instances.unit
import com.sksamuel.elastic4s.ElasticDate.ElasticDateShow
import com.sksamuel.exts.OptionImplicits._

abstract class TimeUnit(val symbol: String)
case object Years extends TimeUnit("y")
case object Months extends TimeUnit("M")
case object Weeks extends TimeUnit("w")
case object Days extends TimeUnit("d")
case object Hours extends TimeUnit("h")
case object Minutes extends TimeUnit("m")
case object Seconds extends TimeUnit("s")

case class Adjustment(value: Int, unit: TimeUnit)

case class ElasticDate(base: String,
                       adjustment: Option[Adjustment] = None,
                       rounding: Option[TimeUnit] = None) {
  def show: String = ElasticDateShow.show(this)
  def add(value: Int, unit: TimeUnit): ElasticDate = copy(adjustment = Adjustment(value, unit).some)
  def subtract(value: Int, unit: TimeUnit): ElasticDate = add(-value, unit)
  def rounding(unit: TimeUnit): ElasticDate = copy(rounding = unit.some)
}

object ElasticDate {

  implicit object ElasticDateShow extends Show[ElasticDate] {
    override def show(t: ElasticDate): String = {
      (t.base match {
        case "now" => "now"
        case date => s"$date||"
      }) +
        t.adjustment.fold("") { adj =>
          val plus = if (adj.value < 0) "" else "+"
          s"$plus${adj.value}${adj.unit.symbol}"
        } +
        t.rounding.fold("")(unit => s"/${unit.symbol}")
    }
  }

  def now: ElasticDate = ElasticDate("now")
  def apply(date: LocalDate): ElasticDate = ElasticDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
}
