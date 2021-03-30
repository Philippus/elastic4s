package com.sksamuel.elastic4s.handlers.cluster

import com.sksamuel.elastic4s.requests.cluster.{NodeUsageRequest, NodeUsageResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler}

object NodeUsageHandler extends Handler[NodeUsageRequest, NodeUsageResponse] {
  override def build(t: NodeUsageRequest): ElasticRequest = {

    val endpoint = t.nodeId match {
      case Some(nodeId) => s"/_nodes/$nodeId/usage"
      case _ => "/_nodes/usage"
    }

    val params = scala.collection.mutable.Map.empty[String, String]
    t.masterTimeout.foreach(params.put("master_timeout", _))
    t.timeout.foreach(params.put("timeout", _))

    ElasticRequest("GET", endpoint, params.toMap)
  }
}
