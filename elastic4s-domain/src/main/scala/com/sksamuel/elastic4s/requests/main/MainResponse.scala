package com.sksamuel.elastic4s.requests.main

import com.fasterxml.jackson.annotation.JsonProperty

case class MainResponse(
    name: String,
    @JsonProperty("cluster_name") clusterName: String,
    @JsonProperty("cluster_uuid") clusterUuid: String,
    version: MainResponse.Version,
    tagline: String
)

object MainResponse {
  case class Version(
      number: String,
      @JsonProperty("build_flavor") buildFlavor: String,
      @JsonProperty("build_type") buildType: String,
      @JsonProperty("build_hash") buildHash: String,
      @JsonProperty("build_date") buildDate: String,
      @JsonProperty("build_snapshot") buildSnapshot: Boolean,
      @JsonProperty("lucene_version") luceneVersion: String,
      @JsonProperty("minimum_wire_compatibility_version") minimumWireCompatibilityVersion: String,
      @JsonProperty("minimum_index_compatibility_version") minimumIndexCompatibilityVersion: String
  )
}
