package com.sksamuel.elastic4s.mappings

import scala.language.implicitConversions

sealed abstract class DynamicMapping

object DynamicMapping {
  case object Strict extends DynamicMapping
  case object Dynamic extends DynamicMapping
  case object False extends DynamicMapping
}

case class RoutingDefinition(required: Boolean,
                             path: Option[String])

