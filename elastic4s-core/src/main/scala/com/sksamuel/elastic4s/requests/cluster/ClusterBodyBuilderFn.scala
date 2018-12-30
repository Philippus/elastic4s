package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ClusterBodyBuilderFn {
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
