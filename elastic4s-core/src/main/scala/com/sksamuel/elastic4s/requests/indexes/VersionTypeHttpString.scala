package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.common.VersionType

object VersionTypeHttpString {
  def apply(versionType: VersionType): String = versionType match {
    case VersionType.External    => "external"
    case VersionType.Internal    => "internal"
    case VersionType.ExternalGte => "external_gte"
  }
}
