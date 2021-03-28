package com.sksamuel.elastic4s.requests.settings

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class UpdateSettingsRequest(indices: Indexes,
                                 preserveExisting: Option[Boolean] = None,
                                 settings: Map[String, String] = Map.empty,
                                 options: Option[IndicesOptionsRequest] = None) {

  // add a new key to the list of settings
  def add(key: String, value: String): UpdateSettingsRequest = copy(settings = settings + (key -> value))

  // add a new key to the list of settings
  def add(kv: (String, String)): UpdateSettingsRequest = copy(settings = settings + kv)

  // replace the settings with this key,value
  def set(kv: (String, String)): UpdateSettingsRequest = copy(settings = Map(kv))

  // replace the settings with this key,value
  def set(key: String, value: String): UpdateSettingsRequest = copy(settings = Map(key -> value))

  // add the map to the list of settings
  def add(map: Map[String, String]): UpdateSettingsRequest = copy(settings = settings ++ map)

  // replace the settings with this map
  def set(map: Map[String, String]): UpdateSettingsRequest = copy(settings = map)

  def preserveExisting(preserveExisting: Boolean): UpdateSettingsRequest =
    copy(preserveExisting = preserveExisting.some)

  def options(options: IndicesOptionsRequest): UpdateSettingsRequest = copy(options = options.some)
}
