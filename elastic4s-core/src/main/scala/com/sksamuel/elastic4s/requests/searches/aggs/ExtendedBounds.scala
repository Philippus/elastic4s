package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ElasticDate

sealed trait ExtendedBounds {
  type Bound
  def min: Bound
  def max: Bound
}

sealed abstract class NumericExtendedBounds extends ExtendedBounds

object ExtendedBounds {
  def apply(min: Long, max: Long): LongExtendedBounds               = LongExtendedBounds(min, max)
  def apply(min: Double, max: Double): DoubleExtendedBounds         = DoubleExtendedBounds(min, max)
  def apply(min: String, max: String): StringExtendedBounds         = StringExtendedBounds(min, max)
  def apply(min: ElasticDate, max: ElasticDate): DateExtendedBounds = DateExtendedBounds(min, max)
}

case class LongExtendedBounds(min: Long, max: Long)               extends NumericExtendedBounds { type Bound = Long        }
case class DoubleExtendedBounds(min: Double, max: Double)         extends NumericExtendedBounds { type Bound = Double      }
case class StringExtendedBounds(min: String, max: String)         extends ExtendedBounds        { type Bound = String      }
case class DateExtendedBounds(min: ElasticDate, max: ElasticDate) extends ExtendedBounds        { type Bound = ElasticDate }
