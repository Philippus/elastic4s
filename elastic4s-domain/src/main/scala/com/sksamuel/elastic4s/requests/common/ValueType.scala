package com.sksamuel.elastic4s.requests.common

sealed trait ValueType

object ValueType {
  case object STRING   extends ValueType
  case object LONG     extends ValueType
  case object DOUBLE   extends ValueType
  case object NUMBER   extends ValueType
  case object NUMERIC  extends ValueType
  case object DATE     extends ValueType
  case object IP       extends ValueType
  case object GEOPOINT extends ValueType
  case object BOOLEAN  extends ValueType
}
