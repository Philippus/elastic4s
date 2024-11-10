package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import com.fasterxml.jackson.annotation.JsonProperty

case class GetIlmStatusResponse(@JsonProperty("operation_mode") operationMode: String)
