package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler}
import com.sksamuel.elastic4s.requests.common.{HealthStatus, Priority}

import com.sksamuel.exts.OptionImplicits._

case class ClusterHealthRequest(indices: Seq[String],
                                timeout: Option[String] = None,
                                waitForActiveShards: Option[Int] = None,
                                waitForEvents: Option[Priority] = None,
                                waitForStatus: Option[HealthStatus] = None,
                                waitForNodes: Option[String] = None,
                                waitForNoRelocatingShards: Option[Boolean] = None) {

  def timeout(value: String): ClusterHealthRequest = copy(timeout = value.some)

  def waitForActiveShards(waitForActiveShards: Int): ClusterHealthRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForEvents(waitForEvents: Priority): ClusterHealthRequest = copy(waitForEvents = waitForEvents.some)

  def waitForStatus(waitForStatus: HealthStatus): ClusterHealthRequest =
    copy(waitForStatus = waitForStatus.some)

  def waitForNodes(waitForNodes: String): ClusterHealthRequest = copy(waitForNodes = waitForNodes.some)

  def waitForNoRelocatingShards(waitForNoRelocatingShards: Boolean): ClusterHealthRequest =
    copy(waitForNoRelocatingShards = waitForNoRelocatingShards.some)
}

case class ClusterHealthResponse(@JsonProperty("cluster_name") clusterName: String,
                                 status: String,
                                 @JsonProperty("timed_out") timeOut: Boolean,
                                 @JsonProperty("number_of_nodes") numberOfNodes: Int,
                                 @JsonProperty("number_of_data_nodes") numberOfDataNodes: Int,
                                 @JsonProperty("active_primary_shards") activePrimaryShards: Int,
                                 @JsonProperty("active_shards") activeShards: Int,
                                 @JsonProperty("relocating_shards") relocatingShards: Int,
                                 @JsonProperty("initializing_shards") initializingShards: Int,
                                 @JsonProperty("unassigned_shards") unassignedShards: Int,
                                 @JsonProperty("delayed_unassigned_shards") delayedUnassignedShards: Int,
                                 @JsonProperty("number_of_pending_tasks") numberOfPendingTasks: Int,
                                 @JsonProperty("number_of_in_flight_fetch") numberOfInFlightFetch: Int,
                                 @JsonProperty("task_max_waiting_in_queue_millis") taskMaxWaitingInQueueMillis: Int,
                                 @JsonProperty("active_shards_percent_as_number") activeShardsPercentAsNumber: Double)
