package com.sksamuel.elastic4s

sealed trait VersionType
object VersionType {

  def valueOf(str: String): VersionType = str.toLowerCase match {
    case "external" => VersionType.External
    case "externalgte" | "external_gte" => VersionType.External
    case "force" => VersionType.Force
    case _ => VersionType.Internal
  }

  case object External extends VersionType
  case object ExternalGte extends VersionType
  case object Force extends VersionType
  case object Internal extends VersionType
}
