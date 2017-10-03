package com.sksamuel.elastic4s.settings

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.admin.IndicesOptions
import com.sksamuel.exts.OptionImplicits._

trait SettingsApi {

  def getSettings(index: String, indexes: String*): GetSettingsDefinition = getSettings(index +: indexes)
  def getSettings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

  def updateSettings(index: String, indexes: String*): UpdateSettingsDefinition = updateSettings(index +: indexes)
  def updateSettings(indexes: Indexes): UpdateSettingsDefinition = UpdateSettingsDefinition(indexes)

  def updateSettings(indexes: Indexes, settings: Map[String, String]) = UpdateSettingsDefinition(indexes, settings = settings)
}

case class GetSettingsDefinition(indexes: Indexes,
                                 options: Option[IndicesOptions] = None) {
  def options(options: IndicesOptions): GetSettingsDefinition = copy(options = options.some)
}

case class UpdateSettingsDefinition(indices: Indexes,
                                    preserveExisting: Option[Boolean] = None,
                                    settings: Map[String, String] = Map.empty,
                                    options: Option[IndicesOptions] = None) {

  // add a new key to the list of settings
  def add(key: String, value: String): UpdateSettingsDefinition = copy(settings = settings + (key -> value))

  // add a new key to the list of settings
  def add(kv: (String, String)): UpdateSettingsDefinition = copy(settings = settings + kv)

  // replace the settings with this key,value
  def set(kv: (String, String)): UpdateSettingsDefinition = copy(settings = Map(kv))

  // replace the settings with this key,value
  def set(key: String, value: String): UpdateSettingsDefinition = copy(settings = Map(key -> value))

  // add the map to the list of settings
  def add(map: Map[String, String]): UpdateSettingsDefinition = copy(settings = settings ++ map)

  // replace the settings with this map
  def set(map: Map[String, String]): UpdateSettingsDefinition = copy(settings = map)

  def preserveExisting(preserveExisting: Boolean): UpdateSettingsDefinition = copy(preserveExisting = preserveExisting.some)

  def options(options: IndicesOptions): UpdateSettingsDefinition = copy(options = options.some)
}
