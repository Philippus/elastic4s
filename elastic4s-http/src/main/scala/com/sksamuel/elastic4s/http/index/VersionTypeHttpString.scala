package com.sksamuel.elastic4s.http.index

import org.elasticsearch.index.VersionType

object VersionTypeHttpString {
  def apply(versionType: VersionType): String = versionType match {
    case VersionType.EXTERNAL => "external"
    case VersionType.INTERNAL => "internal"
    case VersionType.EXTERNAL_GTE => "external_gte"
  }
}
