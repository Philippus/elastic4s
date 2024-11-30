package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty

case class NodeUsageResponse(
    @JsonProperty("_nodes") nodeCounts: NodeCounts,
    @JsonProperty("cluster_name") clusterName: String,
    nodes: Map[String, NodeUsage]
)
