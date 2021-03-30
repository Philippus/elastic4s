package com.sksamuel.elastic4s.requests.common

sealed trait VersionType
object VersionType {

  def valueOf(str: String): VersionType = str.toLowerCase match {
    case "external"                     => VersionType.External
    case "externalgte" | "external_gte" => VersionType.ExternalGte
    case _                              => VersionType.Internal
  }

  case object External    extends VersionType
  case object ExternalGte extends VersionType
  case object Internal extends VersionType

  def EXTERNAL: External.type = External
  def EXTERNAL_GTE: ExternalGte.type = ExternalGte
  def INTERNAL: Internal.type = Internal
}
