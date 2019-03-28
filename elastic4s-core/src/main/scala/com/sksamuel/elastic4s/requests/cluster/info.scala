package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty

case class RemoteClusterInfoRequest()

case class RemoteClusterInfo(seeds: Seq[String],
                             @JsonProperty("http_addresses") httpAddresses: Seq[String],
                             connected: Boolean,
                             @JsonProperty("num_nodes_connected") numNodesConnected: Int,
                             @JsonProperty("max_connections_per_cluster") maxConnectionsPerCluster: Int,
                             @JsonProperty("initial_connect_timeout") initialConnectTimeout: String,
                             @JsonProperty("skip_unavailable") skipUnavailable: Boolean
                            )
