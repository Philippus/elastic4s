package com.sksamuel.elastic4s.requests.settings

import com.sksamuel.elastic4s.Index

case class IndexSettingsResponse(settings: Map[Index, Map[String, String]]) {
  def settingsForIndex(index: Index): Map[String, String] = settings(index)
}
