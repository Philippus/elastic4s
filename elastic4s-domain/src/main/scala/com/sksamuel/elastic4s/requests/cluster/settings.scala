package com.sksamuel.elastic4s.requests.cluster

case class ClusterSettingsRequest(persistentSettings: Map[String, String], transientSettings: Map[String, String]) {

  def persistentSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(persistentSettings = settings)

  def transientSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(transientSettings = settings)
}

case class ClusterSettingsResponse(persistent: Map[String, String], transient: Map[String, String])

case class AddRemoteClusterSettingsRequest(settingsRequest: ClusterSettingsRequest)
case class AddRemoteClusterResponse(persistent: Map[String, Any], transient: Map[String, Any])


