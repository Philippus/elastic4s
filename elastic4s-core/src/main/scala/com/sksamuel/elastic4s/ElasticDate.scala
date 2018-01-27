package com.sksamuel.elastic4s

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.Show
import com.sksamuel.elastic4s.ElasticDate.{ElasticDateMathShow, TimestampElasticDateShow, UnparsedElasticDateShow}
import com.sksamuel.exts.OptionImplicits._

import scala.language.implicitConversions

abstract class TimeUnit(val symbol: String)
case object Years   extends TimeUnit("y")
case object Months  extends TimeUnit("M")
case object Weeks   extends TimeUnit("w")
case object Days    extends TimeUnit("d")
case object Hours   extends TimeUnit("h")
case object Minutes extends TimeUnit("m")
case object Seconds extends TimeUnit("s")

case class Adjustment(value: Int, unit: TimeUnit)

/**
  * Implementations of this trait are used to represent dates and date math
  * in elasticsearch requests.
  *
  * see https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html#date-math
  *
  * There are three types of elastic date you can create.
  *
  * The first is just a wrapped string with no extra help: UnparsedElasticDate("mydate||/d")
  *
  * The second is a wrapped timestamp: TimestampElasticDate(1113232321L)
  *
  * The third and most useful is the ElasticDateMath which allows you to programatically add
  * or subtract values, as well as add a rounding, and it will create the appropriate date string for you.
  * For example, ElasticDate.now.minus(3, Months).add(1, Days).rounding(Weeks)
  */
trait ElasticDate {
  def show: String
}

case class ElasticDateMath(base: String, adjustments: Seq[Adjustment] = Nil, rounding: Option[TimeUnit] = None)
    extends ElasticDate {
  override def show                                         = ElasticDateMathShow.show(this)
  def add(value: Int, unit: TimeUnit): ElasticDateMath      = copy(adjustments = adjustments :+ Adjustment(value, unit))
  def minus(value: Int, unit: TimeUnit): ElasticDateMath    = subtract(value, unit)
  def subtract(value: Int, unit: TimeUnit): ElasticDateMath = add(-value, unit)
  def rounding(unit: TimeUnit): ElasticDateMath             = copy(rounding = unit.some)
}

case class UnparsedElasticDate(value: String) extends ElasticDate {
  override def show = UnparsedElasticDateShow.show(this)
}

case class TimestampElasticDate(timestamp: Long) extends ElasticDate {
  override def show = TimestampElasticDateShow.show(this)
}

object ElasticDate {

  implicit object UnparsedElasticDateShow extends Show[UnparsedElasticDate] {
    override def show(t: UnparsedElasticDate): String = t.value
  }

  implicit object TimestampElasticDateShow extends Show[TimestampElasticDate] {
    override def show(t: TimestampElasticDate): String = t.timestamp.toString
  }

  implicit object ElasticDateMathShow extends Show[ElasticDateMath] {
    override def show(t: ElasticDateMath): String =
      (t.base match {
        case "now" => "now"
        case date  => s"$date||"
      }) + t.adjustments
        .map { adj =>
          val plus = if (adj.value < 0) "" else "+"
          s"$plus${adj.value}${adj.unit.symbol}"
        }
        .mkString("") + t.rounding.fold("")(unit => s"/${unit.symbol}")
  }

  implicit def fromTimestamp(timestamp: Long): TimestampElasticDate = TimestampElasticDate(timestamp)
  implicit def stringToDate(str: String): ElasticDate               = UnparsedElasticDate(str)

  def apply(str: String): ElasticDateMath     = ElasticDateMath(str)
  def now: ElasticDateMath                    = ElasticDateMath("now")
  def apply(date: LocalDate): ElasticDateMath = ElasticDateMath(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
}
