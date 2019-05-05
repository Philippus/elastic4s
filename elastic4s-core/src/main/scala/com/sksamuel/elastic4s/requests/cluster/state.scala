package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty

case class ClusterStateRequest(metrics: Seq[String] = Seq.empty, indices: Seq[String] = Seq.empty) {

  def metrics(metrics: Seq[String]): ClusterStateRequest = copy(metrics = metrics)
  def indices(indices: Seq[String]): ClusterStateRequest = copy(indices = indices)
}

case class ClusterStateResponse(@JsonProperty("cluster_name") clusterName: String,
                                @JsonProperty("cluster_uuid") clusterUUID: String,
                                @JsonProperty("master_node") masterNode: String,
                                @JsonProperty("state_uuid") stateUuid: String,
                                @JsonProperty("nodes") nodes: Map[String, ClusterStateResponse.Node],
                                metadata: Option[ClusterStateResponse.Metadata])

object ClusterStateResponse {

  case class Node(name: String,
                  @JsonProperty("ephemeral_id") ephemeral_id: String,
                  @JsonProperty("transport_address") transportAddress: String)

  case class Index(state: String, aliases: Seq[String])

  case class Metadata(@JsonProperty("cluster_uuid") clusterUuid: String,
                      indices: Map[String, Index],
                      @JsonProperty("cluster_coordination") clusterCoordination: ClusterCoordination)

  case class ClusterCoordination(term: Int,
                                 @JsonProperty("last_committed_config") last_committed_config: List[String],
                                 @JsonProperty("last_accepted_config") last_accepted_config: List[String]
                                )
}


