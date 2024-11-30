package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.settings.{GetSettingsRequest, UpdateSettingsRequest}

trait SettingsApi {

  def getSettings(index: String, indexes: String*): GetSettingsRequest = getSettings(index +: indexes)
  def getSettings(indexes: Indexes): GetSettingsRequest                = GetSettingsRequest(indexes)

  def updateSettings(index: String, indexes: String*): UpdateSettingsRequest = updateSettings(index +: indexes)
  def updateSettings(indexes: Indexes): UpdateSettingsRequest                = UpdateSettingsRequest(indexes)

  def updateSettings(indexes: Indexes, settings: Map[String, String]) =
    UpdateSettingsRequest(indexes, settings = settings)
}
