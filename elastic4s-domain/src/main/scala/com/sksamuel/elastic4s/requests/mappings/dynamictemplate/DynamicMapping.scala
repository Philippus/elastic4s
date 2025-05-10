package com.sksamuel.elastic4s.requests.mappings.dynamictemplate

sealed abstract class DynamicMapping

object DynamicMapping {
  case object Strict  extends DynamicMapping
  case object Dynamic extends DynamicMapping
  case object False   extends DynamicMapping
}
