package com.sksamuel.elastic4s

sealed trait DistanceUnit
object DistanceUnit {
  case object Inch extends DistanceUnit
  case object Yard extends DistanceUnit
  case object Feet extends DistanceUnit
  case object Kilometers extends DistanceUnit
  case object NauticalMiles extends DistanceUnit
  case object Millimeters extends DistanceUnit
  case object Centimeters extends DistanceUnit
  case object Miles extends DistanceUnit
  case object Meters extends DistanceUnit
}
