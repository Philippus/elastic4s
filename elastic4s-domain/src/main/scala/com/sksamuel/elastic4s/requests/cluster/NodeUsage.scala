package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty

case class NodeUsage(timestamp: Long, since: Long, @JsonProperty("rest_actions") restActions: Map[String, Int])
