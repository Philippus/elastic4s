package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty

case class Node(
    name: String,
    @JsonProperty("transport_address") transportAddress: String,
    host: String,
    ip: String,
    roles: Seq[String],
    tasks: Map[String, Task]
)
