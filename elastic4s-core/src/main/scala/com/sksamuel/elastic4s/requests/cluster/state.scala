package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler}

case class ClusterStateRequest(metrics: Seq[String] = Seq.empty, indices: Seq[String] = Seq.empty) {

  def metrics(metrics: Seq[String]): ClusterStateRequest = copy(metrics = metrics)
  def indices(indices: Seq[String]): ClusterStateRequest = copy(indices = indices)
}

case class ClusterStateResponse(@JsonProperty("cluster_name") clusterName: String,
                                @JsonProperty("master_node") masterNode: String,
                                @JsonProperty("compressed_size_in_bytes") compressedSizeInBytes: Long,
                                @JsonProperty("state_uuid") stateUuid: String,
                                metadata: Option[ClusterStateResponse.Metadata])

object ClusterStateResponse {
  case class Index(state: String, aliases: Seq[String])
  case class Metadata(@JsonProperty("cluster_uuid") clusterUuid: String, indices: Map[String, Index])
}


