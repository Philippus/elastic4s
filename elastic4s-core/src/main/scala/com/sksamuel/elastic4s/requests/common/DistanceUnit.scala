package com.sksamuel.elastic4s.requests.common

sealed trait DistanceUnit {
  def meters: Double
  def toMeters(value: Double): Double = value * meters
}
object DistanceUnit {

  val INCH          = Inch
  val YARD          = Yard
  val FEET          = Feet
  val KILOMETERS    = Kilometers
  val NAUTICALMILES = NauticalMiles
  val MILLIMETERS   = Millimeters
  val CENTIMETERS   = Centimeters
  val MILES         = Miles
  val METERS        = Meters

  case object Inch          extends DistanceUnit { val meters = 0.0254   }
  case object Yard          extends DistanceUnit { val meters = 0.9144   }
  case object Feet          extends DistanceUnit { val meters = 0.3048   }
  case object Kilometers    extends DistanceUnit { val meters = 1000.0   }
  case object NauticalMiles extends DistanceUnit { val meters = 1852.0   }
  case object Millimeters   extends DistanceUnit { val meters = 0.001    }
  case object Centimeters   extends DistanceUnit { val meters = 0.01     }
  case object Miles         extends DistanceUnit { val meters = 1609.344 }
  case object Meters        extends DistanceUnit { val meters = 1.0      }
}
