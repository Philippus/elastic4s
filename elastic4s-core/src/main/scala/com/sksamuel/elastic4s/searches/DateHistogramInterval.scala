package com.sksamuel.elastic4s.searches

sealed trait DateHistogramInterval {
  def interval: String
}

object DateHistogramInterval {

  case object Second extends DateHistogramInterval {
    val interval = "1s"
  }

  case object Minute extends DateHistogramInterval {
    val interval = "1m"
  }

  case object Hour extends DateHistogramInterval {
    val interval = "1h"
  }

  case object Day extends DateHistogramInterval {
    val interval = "1d"
  }

  case object Week extends DateHistogramInterval {
    val interval = "1w"
  }

  case object Month extends DateHistogramInterval {
    val interval = "1M"
  }

  case object Quarter extends DateHistogramInterval {
    val interval = "1q"
  }

  case object Year extends DateHistogramInterval {
    val interval = "1y"
  }

  def fromString(str: String): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = str
  }

  def seconds(seconds: Long): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = s"${seconds}s"
  }

  def minutes(mins: Long): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = s"${mins}m"
  }

  def hours(hours: Long): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = s"${hours}h"
  }

  def days(days: Long): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = s"${days}d"
  }

  def weeks(weeks: Long): DateHistogramInterval = new DateHistogramInterval {
    override def interval: String = s"${weeks}w"
  }
}
