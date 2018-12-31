package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, XContentBuilder, XContentFactory}

case class ClusterSettingsRequest(persistentSettings: Map[String, String], transientSettings: Map[String, String]) {

  def persistentSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(persistentSettings = settings)

  def transientSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(transientSettings = settings)
}

case class ClusterSettingsResponse(persistent: Map[String, String], transient: Map[String, String])

object ClusterSettingsBodyBuilderFn {
  def apply(request: ClusterSettingsRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    if(request.persistentSettings.nonEmpty) {
      builder.startObject("persistent")
      request.persistentSettings.foreach(t ⇒ builder.field(t._1, t._2))
      builder.endObject()
    }

    if(request.transientSettings.nonEmpty) {
      builder.startObject("transient")
      request.transientSettings.foreach(t ⇒ builder.field(t._1, t._2))
      builder.endObject()
    }
    builder
  }
}
