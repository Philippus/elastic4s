package com.sksamuel.elastic4s.requests.reloadsearchanalyzers

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards

case class ReloadDetails(index: String, @JsonProperty("reloaded_analyzers") reloadedAnalyzers: Seq[String], @JsonProperty("reloaded_node_ids") reloadedNodeIds: Seq[String])

case class ReloadSearchAnalyzersResponse(@JsonProperty("reload_details") reloadDetails: Seq[ReloadDetails],
                                         @JsonProperty("_shards") shards: Option[Shards])
