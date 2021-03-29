package com.sksamuel.elastic4s.handlers.nodes

import com.sksamuel.elastic4s.requests.nodes.{NodeInfoRequest, NodeInfoResponse, NodeStatsRequest, NodesStatsResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler}

trait NodesHandlers {

  implicit object NodeInfoHandler extends Handler[NodeInfoRequest, NodeInfoResponse] {
    override def build(request: NodeInfoRequest): ElasticRequest = {
      val endpoint =
        if (request.nodes.isEmpty)
          "/_nodes/"
        else
          "/_nodes/" + request.nodes.mkString(",")
      ElasticRequest("GET", endpoint)
    }
  }

  implicit object NodeStatsHandler extends Handler[NodeStatsRequest, NodesStatsResponse] {
    override def build(request: NodeStatsRequest): ElasticRequest = {
      val endpoint =
        if (request.nodes.nonEmpty)
          "/_nodes/" + request.nodes.mkString(",") + "/stats/" + request.stats.mkString(",")
        else
          "/_nodes/stats/" + request.stats.mkString(",")
      ElasticRequest("GET", endpoint)
    }
  }
}
