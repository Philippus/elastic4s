package com.sksamuel.elastic4s.requests.searches.queries

sealed trait RegexpFlag

object RegexpFlag {
  case object Intersection extends RegexpFlag
  case object Complement extends RegexpFlag
  case object Empty extends RegexpFlag
  case object AnyString extends RegexpFlag
  case object Interval extends RegexpFlag
  case object None extends RegexpFlag
  case object All extends RegexpFlag
}
